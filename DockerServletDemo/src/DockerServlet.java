

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.PrintWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servlet implementation class DockerServlet
 */
@WebServlet("/DockerServlet")
public class DockerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DockerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String requestUrl = request.getRequestURI();
		out.println(requestUrl);

		if (requestUrl.equals("/DockerServlet/runningconts")) {
			ProcessBuilder builder = new ProcessBuilder("docker", "ps");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String total_output = "";
			String line = null;
			while ((line = reader.readLine()) != null) {
				total_output += line + "\n";
			}
			List<String> list = new ArrayList<String>(Arrays.asList(total_output.split("\n")));
			
			JSONObject id_obj = new JSONObject();			
			JSONArray id_list = new JSONArray();
			
			for (int i = 1; i < list.size(); i++) {
				List<String> items = new ArrayList<String>(Arrays.asList(list.get(i).split(" ")));
				if (items.size() > 0) {
					id_list.add(items.get(0));
				}
				
			}

			id_obj.put("containers", id_list);
			out.println(id_obj.toJSONString());
			

		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestUrl = request.getRequestURI();
		if (requestUrl.equals("/DockerServlet/stop")) {
			String container_id = request.getParameter("contid");
			boolean matches = Pattern.matches("[a-f0-9]", container_id);
			if (matches) {
				ProcessBuilder builder = new ProcessBuilder("docker", "stop", container_id);
				builder.redirectErrorStream(true);
				Process process = builder.start();
				InputStream is = process.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				String total_output = "";
				String line = null;
				while ((line = reader.readLine()) != null) {
				   total_output += line;
				}
			}
		}
	}

}
