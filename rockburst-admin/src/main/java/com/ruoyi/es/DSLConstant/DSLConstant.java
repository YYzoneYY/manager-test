package com.ruoyi.es.DSLConstant;

public class DSLConstant {

    /**
     * 创建矿压索引库DSL
     */
    public static final String MINE_PRESSURE_DSL = "{\n" +
            "    \"mappings\":{\n" +
            "        \"properties\":{\n" +
            "           \"monitoringCode\":{\n" +
            "               \"type\":\"keyword\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"sensorType\":{\n" +
            "               \"type\":\"keyword\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"sensorLocation\":{\n" +
            "               \"type\":\"keyword\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"monitoringValue\":{\n" +
            "               \"type\":\"double\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"valueShallow\":{\n" +
            "               \"type\":\"double\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"valueSecond\":{\n" +
            "               \"type\":\"double\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"electromagnetismMaxValue\":{\n" +
            "               \"type\":\"double\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"electromagneticPulse\":{\n" +
            "               \"type\":\"double\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"monitoringStatus\":{\n" +
            "               \"type\":\"keyword\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"dataTime\":{\n" +
            "               \"type\":\"long\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"tag\":{\n" +
            "               \"type\":\"keyword\",\n" +
            "               \"copy_to\":\"all\"\n" +
            "           },\n" +
            "           \"all\":{\n" +
            "               \"type\":\"keyword\"\n" +
            "           }\n" +
            "        }\n" +
            "    }\n" +
            "}";
}
