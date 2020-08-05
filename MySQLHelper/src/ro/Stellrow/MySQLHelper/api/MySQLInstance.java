package ro.Stellrow.MySQLHelper.api;

import io.netty.util.concurrent.CompleteFuture;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class MySQLInstance{
    private String host;
    private Integer port;
    private String dataBase;
    private String username;
    private String password;
    private Connection connection;
    private Statement statement;
    private final Plugin pluginInstance;

    public MySQLInstance(Plugin pluginInstance,String host, Integer port, String dataBase, String username, String password){
        this.host = host;
        this.port = port;
        this.dataBase = dataBase;
        this.username = username;
        this.password = password;
        this.pluginInstance=pluginInstance;
            asyncExecute(() -> {
                try{
                openConnection();
                statement = connection.createStatement();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            });
        }
    public void sendStatement(String statementToExecute){
        asyncExecute(() -> {
            try {
                statement.execute(statementToExecute);
            }catch (SQLException exception){
                exception.printStackTrace();
            }
        });
    }
    public void createTable(String statementToExecute){
        asyncExecute(() -> {
            try {
                statement.execute(statementToExecute);
            }catch (SQLException exception){
                exception.printStackTrace();
            }
        });

    }
    public CompletableFuture<ResultSet> getResult(String path){
        return CompletableFuture.supplyAsync(() -> {
            try {
                return statement.executeQuery(path);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public void setData(String statementToExecute){
        asyncExecute(() -> executeQuery(statementToExecute));
    }
    private void openConnection() throws SQLException,ClassNotFoundException{
        if(connection!=null&&!connection.isClosed()){
            return;
        }
        synchronized (this){
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.dataBase, this.username, this.password);
        }
        }
        //Execute statement on other threads
    private void asyncExecute(StatementSQL statement){
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                statement.execute();
            }
        };

        runnable.runTaskAsynchronously(pluginInstance);

    }
    private interface StatementSQL{
        void execute();
    }
    private void executeQuery(String toExecute){
        try {
            statement.executeQuery(toExecute);
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    }

