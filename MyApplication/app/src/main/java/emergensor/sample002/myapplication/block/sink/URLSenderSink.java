package emergensor.sample002.myapplication.block.sink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import emergensor.sample002.myapplication.lib.Consumer;

/**
 * 指定のURLに文字列をPOSTするだけ。
 */
public class URLSenderSink implements Consumer<String> {

    private final String name;
    private final String password;
    private final URL url;

    public URLSenderSink(String name, String password, URL url) {
        this.name = name;
        this.password = password;
        this.url = url;
    }

    @Override
    public void accept(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(name, password.toCharArray());
                    }
                });

                System.err.println("Start send");

                try {
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    try (PrintStream out = new PrintStream(connection.getOutputStream())) {
                        out.print(text);
                    }
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        System.err.println(in.readLine());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
