package com.qiqilm.server.admin.task.beat;


public class GameDataTableHelp {
    public static String getGameDataByAgent(String agent, String dayFormat){
        return "game_data".concat(agent.concat("_").concat(dayFormat));
    }


    public static String getGameDataByTime(String agent, String game_end_time){
        String day  = game_end_time.substring(0,10).replace("-","");
        return  getGameDataByAgent(agent,day);
    }

    public static String getGameDataByDayString(String agent, String dayString){
        String day  = dayString.replace("-","");
        return  getGameDataByAgent(agent,day);
    }

    public static void main(String[] args) {
        System.out.println("2020-11-24 23:59:34".substring(0,10));

        System.out.println(getGameDataByTime("7701","2020-11-24 23:59:34"));
    }
}
