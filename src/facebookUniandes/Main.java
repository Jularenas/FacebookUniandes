package facebookUniandes;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Main {
	
	private String token;
	
	public Main(String token){
		this.token=token;
	}
	
	public URL crearSolicitudAlAPI(String id_page ) throws MalformedURLException{
		URL url = new URL("https://graph.facebook.com/v2.11/" +  id_page +"/posts?format=json&limit=100&access_token=" + token);
		//		System.out.println(url);
		return url;
	}
	public String enviarSolicitud(URL url) throws IOException{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.connect();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String resultado = new String();
		String line;
		while ((line = reader.readLine()) != null) {
			resultado += line + "\n";
		}
		connection.disconnect();
		System.out.println(resultado);
		return resultado;

	}
	
	public static void main(String[] args)
	{
		try(PrintWriter op= new PrintWriter(new FileWriter(new File("./data/responses"))))
		{
			while(true){
				Scanner s= new Scanner(System.in);
				String token=s.nextLine();
				String id_page=s.nextLine();
				
				op.println(token);
				op.println(id_page);
				op.println("------------------------");
				op.println("------------------------");
				op.println("------------------------");
				op.println("------------------------");
				
				Main m= new Main(token);
				try {
					String response=m.enviarSolicitud(m.crearSolicitudAlAPI(id_page));
					String[] responseSegments=response.split("},");
					for (int i = 0; i < responseSegments.length; i++) {
						op.println(responseSegments[i]);
					}
					} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
		}
		catch(Exception e)
		{
			
		}
	}

}
