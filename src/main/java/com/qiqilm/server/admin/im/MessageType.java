package com.qiqilm.server.admin.im;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qiqilm.server.admin.exception.BaseException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@Data
@Log4j2
public class MessageType {

    private String groupId ;
    private MessageEnum msgEnmu;
    private Object[] data;


    public static MessageType setMsgEnmu(MessageEnum msgEnmu) {
        MessageType type  = new MessageType();
        type.msgEnmu = msgEnmu;
        return type;
    }

    public MessageType setData( Object ... args){
        if(args.length<4){
            data = new Object[4];
            for (int i = 0; i < args.length; i++) {
                data[i] = args[i];
            }
            for (int i = args.length; i < 4; i++) {
                data[i] = "";
            }
        }else {
            data = args;
        }
        return this;
    }

    public ObjectNode getNode(){
        if(Objects.isNull(data))
            throw new BaseException( "获取节点失败" );
        return ofNode(data);
    }

   public ObjectNode ofNode(Object ... args){
        if(Objects.isNull(data))
            setData(args);
       final ObjectMapper mapper = new ObjectMapper();
       final ObjectNode node = mapper.createObjectNode();
       node.put("MsgType",msgEnmu.getVal());
       final ObjectNode content = mapper.createObjectNode();

       try {
           switch (msgEnmu.getVal()){
                case "TIMTextElem":
                    content.put("Text",data[0].toString());
                    break;
               case "TIMLocationElem":
                    content.put("Desc",data[0].toString());
                    content.put("Latitude",Double.valueOf(data[1].toString()));
                    content.put("Longitude",Double.valueOf(data[2].toString()));
                    break;
               case "TIMFaceElem":
                   content.put("Index",Integer.valueOf(data[0].toString()));
                   content.put("Data",data[1].toString());
                   break;
               case "TIMCustomElem":
                   content.put("Data",data[0].toString());
                   content.put("Desc",data[1].toString());
                   content.put("Ext",data[2].toString());
                   content.put("Sound",data[3].toString());
                   break;
               default:
                   return null;
            }
       }catch (Exception e){
           log.error( e.getMessage(), e );
           throw new BaseException( "未初始化消息体" );
       }

       node.put("MsgContent",content);
       return node;
   }
}
