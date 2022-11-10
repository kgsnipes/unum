package com.unum.impl;

import com.unum.UniqueNumberGenerator;
import com.unum.exception.UnumException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
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
            if(!tableExists(this.dbConnection,this.getTableName()))
            {
                this.createTable();
                this.registerCurrentPool(this.startPoint);
            }
            else {
                this.registerCurrentPool(-1);
            }

        }
        catch (Exception ex)
        {
            throw new UnumException(ex.getMessage(),ex);
        }

    }

    protected String getTableName()
    {
        return NUMBER_GENERATOR_TABLE+ "_"+this.generatorIdentifier;
    }

    private void registerCurrentPool(int startPoint) throws SQLException, UnumException {
        if(startPoint>-1)
        {
            this.startPoint=this.getRecentEndingNumber();
        }
        else {

        }

    }

    private void addNewEntryInTable()
    {
        
    }

    private Integer getRecentEndingNumber() throws UnumException, SQLException {
        Integer recentNumber=this.getUpperLimit();
        Statement stmt=this.dbConnection.createStatement();
        ResultSet rs=stmt.executeQuery("SELECT * FROM "+getTableName()+" ORDER BY timestamp desc");
        if(rs.next())
        {
            recentNumber=rs.getInt("lastnumber");
        }

        if(recentNumber>=Integer.MAX_VALUE-1 && recentNumber<Integer.MAX_VALUE)
        {
            throw new UnumException("cannot increment more reached limit");
        }
        else {
            recentNumber=recentNumber+1;
        }
        return recentNumber;
    }

    private void createTable() throws SQLException {

        Statement stmt = this.dbConnection.createStatement();
        String sql = "CREATE TABLE " +getTableName()+
        "(startNumber INTEGER " +
                " lastNumber INTEGER, " +
                " timestamp BIGINT)";
        log.info("Executing SQL : "+ sql);
        stmt.executeUpdate(sql);

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
