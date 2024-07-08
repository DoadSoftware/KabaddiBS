package com.kabaddi.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.kabaddi.model.Api_Match;
import com.kabaddi.model.Api_pre_match;
import com.kabaddi.model.Clock;
import com.kabaddi.model.Configurations;
import com.kabaddi.model.Event;
import com.kabaddi.model.EventFile;
import com.kabaddi.model.Match;
import com.kabaddi.service.KabaddiService;
import com.kabaddi.util.KabaddiFunctions;
import com.kabaddi.util.KabaddiUtil;
import com.kabaddi.broadcaster.KABADDI;
import com.kabaddi.containers.Scene;
import com.kabaddi.containers.ScoreBug;

import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	KabaddiService kabaddiService;
	
	public static String expiry_date = "2024-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static KABADDI this_Kabaddi;
	public static Match session_match;
	public static EventFile session_event;
	public static Clock session_clock = new Clock();
	public static Configurations session_Configurations;
	
	List<Scene> session_selected_scenes = new ArrayList<Scene>();
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static PrintWriter print_writer;
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) throws JAXBException, IOException, ParseException 
	{		
		if(current_date == null || current_date.isEmpty()) {
			current_date = KabaddiFunctions.getOnlineCurrentDate();
		}
		model.addAttribute("session_viz_scenes", new File(KabaddiUtil.KABADDI_DIRECTORY + 
				KabaddiUtil.SCENES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".via") && pathname.isFile();
		    }
		}));

		model.addAttribute("match_files", new File(KabaddiUtil.KABADDI_DIRECTORY 
				+ KabaddiUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".xml") && pathname.isFile();
		    }
		}));
		
		if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY + KabaddiUtil.BIGSCREEN_XML).exists()) {
			session_Configurations = (Configurations)JAXBContext.newInstance(Configurations.class).createUnmarshaller().unmarshal(
				new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY + KabaddiUtil.BIGSCREEN_XML));
		} else {
			session_Configurations = new Configurations();
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, 
				new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY + KabaddiUtil.BIGSCREEN_XML));
		}
		
		model.addAttribute("session_Configurations",session_Configurations);
	
		return "initialise";
	}
	
	@RequestMapping(value = {"/match"}, method = {RequestMethod.POST,RequestMethod.GET})
	public String kabaddiMatchPage(ModelMap model, 
		@RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster,
		@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
		@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber)
			throws IOException, ParseException, JAXBException, InterruptedException  
	{
		if(current_date == null || current_date.isEmpty()) {
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
			
			model.addAttribute("match_files", new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
				@Override
			    public boolean accept(File pathname) {
			        String name = pathname.getName().toLowerCase();
			        return name.endsWith(".xml") && pathname.isFile();
			    }
			}));

			model.addAttribute("licence_expiry_message",
				"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
				new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
			
			session_match = new Match();
			session_event = new EventFile();
			if(session_event.getEvents() == null || session_event.getEvents().size() <= 0)
				session_event.setEvents(new ArrayList<Event>());
			
			session_Configurations.setBroadcaster(selectedBroadcaster);
			session_Configurations.setIpAddress(vizIPAddresss);
			
			if(!vizPortNumber.trim().isEmpty()) {
				session_Configurations.setPortNumber(Integer.valueOf(vizPortNumber));
				session_socket = new Socket(vizIPAddresss, Integer.valueOf(vizPortNumber));
				print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			}
			session_selected_broadcaster = selectedBroadcaster;
			
			switch (session_selected_broadcaster.toUpperCase()) {
			case KabaddiUtil.KABADDI:
				session_selected_scenes.add(new Scene("D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_UPKL_2024\\Scenes\\Big_Screen.sum",KabaddiUtil.ONE)); // Front layer
				//session_selected_scenes.add(new Scene(KabaddiUtil.BG_SCENE_PATH,KabaddiUtil.THREE));
				session_selected_scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
				//session_selected_scenes.get(1).scene_load(print_writer, session_selected_broadcaster);
				this_Kabaddi = new KABADDI();
				this_Kabaddi.scorebug = new ScoreBug();
				break;
			}
			
			JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, 
				new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY + KabaddiUtil.BIGSCREEN_XML));
			
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			
			return "match";
		}
	}
	
	@RequestMapping(value = {"/processKabaddiProcedures"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processKabaddiProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)
					throws JAXBException, IllegalAccessException, InvocationTargetException, IOException, NumberFormatException, InterruptedException
	{	
		switch (whatToProcess.toUpperCase()) {
		/*case "READ_CLOCK":
			
			if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON).exists()) {
				session_clock = new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + 
						KabaddiUtil.CLOCK_JSON), Clock.class);

				session_match.setClock(session_clock);
				
				if(session_selected_broadcaster != null) {
					switch (session_selected_broadcaster) {
					case KabaddiUtil.KABADDI:
						this_Kabaddi.updateScoreBug(session_selected_scenes, session_match, print_writer);
						break;
					}
				}
			}
			
			return JSONObject.fromObject(session_match).toString();*/
			
		case KabaddiUtil.LOAD_MATCH:
			session_match = KabaddiFunctions.populateMatchVariables(kabaddiService, (Match) JAXBContext.newInstance(Match.class).createUnmarshaller()
					.unmarshal(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY + valueToProcess)));
			session_event = (EventFile) JAXBContext.newInstance(EventFile.class).createUnmarshaller().unmarshal(
					new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.EVENT_DIRECTORY + valueToProcess));
			session_match.setEvents(session_event.getEvents());
			
			if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() + "-in-match" + KabaddiUtil.JSON_EXTENSION).exists()) {
				session_match.setApi_Match(new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() 
					+ "-in-match" + KabaddiUtil.JSON_EXTENSION), Api_Match.class));
			}
			
			if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + "pre-match_10m" + KabaddiUtil.JSON_EXTENSION).exists()) {
				ObjectMapper objectMapper = new ObjectMapper();
				
				try {
		            String filePath = KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + "pre-match_10m" + KabaddiUtil.JSON_EXTENSION;
		            File file = new File(filePath);
		            
		            List<Api_pre_match> apiPreMatchList = objectMapper.readValue(file, new TypeReference<List<Api_pre_match>>() {});

		            session_match.setApi_PreMatch(apiPreMatchList);

		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
			
			if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON).exists()) {
				session_match.setClock(new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + 
						KabaddiUtil.CLOCK_JSON), Clock.class));
			} else {
				session_match.setClock(new Clock());
			}
			
			return JSONObject.fromObject(session_match).toString();

		case "READ-MATCH-AND-POPULATE":
			
			if(session_match != null && !valueToProcess.equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
					new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).lastModified())))
			{
				session_match = KabaddiFunctions.populateMatchVariables(kabaddiService, (Match) JAXBContext.newInstance(Match.class).createUnmarshaller()
						.unmarshal(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY + session_match.getMatchFileName())));
				
				session_match.setMatchFileTimeStamp(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
						new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).lastModified()));
				
				session_event = (EventFile) JAXBContext.newInstance(EventFile.class).createUnmarshaller().unmarshal(
						new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.EVENT_DIRECTORY + session_match.getMatchFileName()));
				session_match.setEvents(session_event.getEvents());
			}
			
			if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON).exists()) {
				
//		        try {
//		        	session_match.setClock(new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON), Clock.class));
//		        } catch (MismatchedInputException e) {
//		            System.err.println("No content to map due to end-of-input");
//		        } catch (IOException e) {
//		            e.printStackTrace();
//		        }
		        
	        	session_match.setClock(new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON), Clock.class));

			}
			
			if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() + "-in-match" + KabaddiUtil.JSON_EXTENSION).exists()) {
				session_match.setApi_Match(new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() 
					+ "-in-match" + KabaddiUtil.JSON_EXTENSION), Api_Match.class));
			}
			
			if(session_selected_broadcaster != null) {
				switch (session_selected_broadcaster) {
				case KabaddiUtil.KABADDI:
					this_Kabaddi.updateScoreBug(session_selected_scenes, session_match, print_writer);
					break;
				}
			}
			
			return JSONObject.fromObject(session_match).toString();
			
		default:
			
			switch (session_selected_broadcaster) {
			case KabaddiUtil.KABADDI:
				this_Kabaddi.ProcessGraphicOption(whatToProcess, session_match, kabaddiService, print_writer, session_selected_scenes,valueToProcess);
				break;
			}
			return JSONObject.fromObject(session_match).toString();
		}
	}
}