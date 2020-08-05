package ro.Stellrow.MySQLHelper.api;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

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
    private void openConnection() throws SQLException {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dataBase, this.username, this.password);
            }catch (SQLException | ClassNotFoundException exception){
                exception.printStackTrace();
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
    }

