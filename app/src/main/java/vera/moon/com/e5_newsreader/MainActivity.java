package vera.moon.com.e5_newsreader;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String jsonres;

    Map<Integer,String> articlesurl = new HashMap<Integer, String>();
    Map<Integer,String> articleTitles = new HashMap<Integer, String>();

    ArrayList<Integer> articleIds = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadT down = new DownloadT();
        try {
            //Get toptopics
            jsonres = down.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();

            JSONArray json = new JSONArray(jsonres);

            for(int i=0;i<20;i++){
                DownloadT newT = new DownloadT();

                //We use this to save in the DB
                String article = json.getString(i);

                //From each articleid get the json
                String articleinfo = newT.execute("https://hacker-news.firebaseio.com/v0/item/"+article+".json?print=pretty").get();
                JSONObject objet = new JSONObject(articleinfo);

                String title = objet.getString("title");
                String articleurl = objet.getString("url");

                articleIds.add(Integer.parseInt(article));
                articleTitles.put(Integer.parseInt(article),title);
                articlesurl.put(Integer.parseInt(article),articleurl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DownloadT extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String res = "";
            URL url;
            HttpURLConnection connection;

            try{

                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){

                    res += (char)data;
                    data = reader.read();
                }

                return res;

            }catch(Exception e){}

            return res;
        }
    }
}
