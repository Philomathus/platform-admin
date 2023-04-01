package com.qiqilm.server.admin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.qiqilm.server.admin.core.vo.AjaxResult;
import com.qiqilm.server.admin.domain.ConfigGametype;
import com.qiqilm.server.admin.domain.GamePlatform;
import com.qiqilm.server.admin.mapper.ConfigGametypeMapper;
import com.qiqilm.server.admin.mapper.GamePlatformMapper;
import com.qiqilm.server.admin.service.IConfigGametypeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author 77tv
 * @date 2021-01-26
 */
@Service
public class ConfigGametypeServiceImpl implements IConfigGametypeService {
    @Autowired
    private ConfigGametypeMapper configGametypeMapper;
    @Autowired
    private GamePlatformMapper gamePlatformMapper;
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ConfigGametype selectConfigGametypeById(String id) {
        ConfigGametype configGametype = configGametypeMapper.selectConfigGametypeById(id);
//        configGametype.setPlatformName(configGametype.getPlatformId());
        return configGametype;
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param configGametype 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ConfigGametype> selectConfigGametypeList(ConfigGametype configGametype) {
        return configGametypeMapper.selectConfigGametypeList(configGametype);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param configGametype 【请填写功能名称】
     * @return 结果
     */
    @Override
    public AjaxResult insertConfigGametype(ConfigGametype configGametype) {

        GamePlatform gamePlatform= gamePlatformMapper.findAgentList(configGametype);
        configGametype.setPlatformId( gamePlatform.getAgent() );
        configGametype.setPlatformName(gamePlatform.getName());
        String id= gamePlatform.getAgent() + "-" + configGametype.getSonPlatformId();
        ConfigGametype gametype = configGametypeMapper.selectConfigGametypeById(id);
        if (Objects.isNull(gametype)){
            configGametype.setId(id);
            configGametypeMapper.insertConfigGametype(configGametype);
            return AjaxResult.success("新增成功");
        }else {
            return AjaxResult.success("新增重复");
        }

    }

    /**
     * 修改【请填写功能名称】
     *
     * @param configGametype 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateConfigGametype(ConfigGametype configGametype) {
        return configGametypeMapper.updateConfigGametype(configGametype);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigGametypeByIds(String[] ids) {
        return configGametypeMapper.deleteConfigGametypeByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteConfigGametypeById(String id) {
        return configGametypeMapper.deleteConfigGametypeById(id);
    }

    @Override
    public AjaxResult batchExcel( @RequestParam( "excelFile" ) MultipartFile excelFile ) {
        Workbook workbook = null;
        StringBuilder userId   = new StringBuilder();

        try {
            //Grab List in GamePlatform
            List<GamePlatform> gamePlatformList = gamePlatformMapper.selectGamePlatformList( new GamePlatform() );
            workbook = WorkbookFactory.create( excelFile.getInputStream() );
            excelFile.getInputStream().close();
            Sheet sheet = workbook.getSheetAt( 0 );
            int rowLength = sheet.getLastRowNum() + 1;
            Row row;
            //start at row 1 as row 0 is headers
            for ( int i = 1; i < rowLength; i++ ) {
                Cell cell;
                row = sheet.getRow( i );
                String cell1; //platform_id + sub_platform_id
                String cell2; //platform_id
                String cell3 = null; //platform_name
                String cell4 = null; //sub_platform_id
                String cell5 = null; //sub_platform_name

                for ( int j = 0; j < 3; j++ ) {
                    cell = row.getCell( j );
                    if ( cell != null ) {
                        cell.setCellType( CellType.STRING );
                        String data = cell.getStringCellValue();
                        if ( j == 0 ) {
                            cell3 = data.trim();
                        } else if ( j == 1 ) {
                            cell4 = data.trim();
                        } else {
                            cell5 = data.trim();
                        }
                    }
                }

                final String name = cell3;
                GamePlatform gamePlatform = gamePlatformList.stream().filter( item -> item.getName().equals( name ) ).findFirst().orElse( null );
                if( gamePlatform != null ){
                    cell2 = gamePlatform.getAgent();
                    cell1 = cell2 + "-" + cell4;
                    userId = userId.append( "\"" )
                            .append( cell1 ).append( "\"" ).append( "," ).append( "\"" )
                            .append( cell2 ).append( "\"" ).append( "," ).append( "\"" )
                            .append( cell3 ).append( "\"" ).append( "," ).append( "\"" )
                            .append( cell4 ).append( "\"" ).append( "," ).append( "\"" )
                            .append( cell5 ).append( "\"" ).append( "),(" );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        userId = new StringBuilder( userId.substring( 0, userId.length() - 3 ) );
        String userIds = String.valueOf( userId );
        //清除表中数据
        configGametypeMapper.insertExcelSheet( userIds );
        return AjaxResult.success();
    }

}