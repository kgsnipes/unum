package com.unum.impl;

import com.unum.UniqueNumberGenerator;
import com.unum.exception.UnumException;

import java.sql.*;

public class PersistableUniqueNumberGeneratorImpl implements UniqueNumberGenerator {

    private final static String NUMBER_GENERATOR_TABLE="number_generator_table";

    private Connection dbConnection;

    private UniqueNumberGenerator numberGenerator;

    private Integer generatorIdentifier;

    private Integer instance;

    private Integer startPoint;

    private Integer poolsize;

    public PersistableUniqueNumberGeneratorImpl(Connection connection,int generatorIdentifier, int instance, int startPoint, int poolsize) throws UnumException {
        this.dbConnection=connection;
        this.generatorIdentifier=generatorIdentifier;
        this.instance=instance;
        this.startPoint=startPoint;
        this.poolsize=poolsize;
        this.init();


    }

    private void init() throws UnumException {
        try {
            this.initTableIfNotExists();

        }
        catch (Exception ex)
        {
            throw new UnumException(ex.getMessage(),ex);
        }

    }

    private void registerCurrentPool()
    {

    }

    private void initTableIfNotExists() throws SQLException, UnumException {

        if(!tableExists(this.dbConnection,NUMBER_GENERATOR_TABLE))
        {
            Statement stmt = this.dbConnection.createStatement();
            String sql = "CREATE TABLE " +NUMBER_GENERATOR_TABLE+ "_"+this.generatorIdentifier+
            "(startNumber INTEGER " +
                    " lastNumber INTEGER, " +
                    " timestamp BIGINT)";

            stmt.executeUpdate(sql);
        }
    }

    boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
        return resultSet.next();
    }

    @Override
    public int getNext() throws UnumException {
        return this.numberGenerator.getNext();
    }

    @Override
    public void resumeFrom(int number) throws UnumException {
        this.numberGenerator.resumeFrom(number);
    }

    @Override
    public int getUpperLimit() throws UnumException {
        return this.numberGenerator.getUpperLimit();
    }

    @Override
    public int getCapacityAvailable() throws UnumException {
        return this.numberGenerator.getCapacityAvailable();
    }
}
