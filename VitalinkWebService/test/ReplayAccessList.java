import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by bdcuyp0 on 4-11-2016.
 */
public class ReplayAccessList {
    public static void main(String[] args) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(args[0]));
            String line = reader.readLine();
            while (line != null) {
                String[] elements = line.split("\\s");
                final String request = elements[6];
                Thread.sleep(Integer.parseInt(args[1]));
                final Runnable target = new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://playkws-jboss" + request;
                        HttpMethod get = new GetMethod(url);
                        try {

                            String username = "bdcuyp0";
                            String password = "*******";

                            HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());


                            client.getState().setCredentials(
                                              AuthScope.ANY,
                                               new UsernamePasswordCredentials(username, password)
                                    );

                            get.setDoAuthentication(true);


                            System.out.println("HttpTools.httpGet: " + url);

                            int statusCode = client.executeMethod(get);
                            final Header[] headers = get.getRequestHeaders();
                            System.out.println("HttpTools.httpGet: " + statusCode);

                        } catch (IOException e) {
                            System.out.println(request + "failed");
                        } finally {
                            get.releaseConnection();
                        }
                    }
                };
                new Thread(target).start();
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

    }
}
