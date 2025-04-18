package com.kabaddi.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParser.Feature;

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
import com.kabaddi.broadcaster.KABADDI_GIPKL;
import com.kabaddi.broadcaster.KABADDI_GIPKL_AR;
import com.kabaddi.containers.Scene;
import com.kabaddi.containers.ScoreBug;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	KabaddiService kabaddiService;
	
	public static String expiry_date = "2025-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static KABADDI this_Kabaddi;
	public static KABADDI_GIPKL this_GIPKL;
	public static KABADDI_GIPKL_AR this_GIPKL_AR = new KABADDI_GIPKL_AR();
	public static Match session_match;
	public static EventFile session_event;
	public static Clock session_clock = new Clock();
	public static Configurations session_Configurations = new Configurations() ;
	public static  Match SwapMatch = new Match();
	public static Api_pre_match api_pre_match = new Api_pre_match();
	public static int counter;
	
	List<Scene> session_selected_scenes = new ArrayList<Scene>();
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static PrintWriter print_writer;
	
	public static ObjectMapper objectMapper = new ObjectMapper();
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) throws JAXBException, IOException, ParseException 
	{		
//		if(current_date == null || current_date.isEmpty()) {
//			current_date = KabaddiFunctions.getOnlineCurrentDate();
//		}

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
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		
		model.addAttribute("configuration_files", new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".xml") && pathname.isFile();
		    }
		}));
		model.addAttribute("session_Configurations",session_Configurations);
	
		return "initialise";
	}
	
	@RequestMapping(value = {"/match"}, method = {RequestMethod.POST,RequestMethod.GET})
	public String kabaddiMatchPage(ModelMap model, 
		@RequestParam(value = "selectedBroadcaster", required = false, defaultValue = "") String selectedBroadcaster,
		@RequestParam(value = "configuration_file_name", required = false, defaultValue = "") String configuration_file_name,
		@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddresss,
		@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber)
			throws IOException, ParseException, JAXBException, InterruptedException  
	{
		model.addAttribute("match_files", new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		System.out.println(selectedBroadcaster);
		session_Configurations = new Configurations(selectedBroadcaster, vizIPAddresss, Integer.valueOf(vizPortNumber), "", 0, "");
		
		JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, 
				new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY + configuration_file_name));

//		model.addAttribute("licence_expiry_message",
//			"Software licence expires on " + new SimpleDateFormat("E, dd MMM yyyy").format(
//			new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date)));
		
		objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
		
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
		session_selected_scenes.clear();
		switch (session_selected_broadcaster.toUpperCase()) {
		
		case "KABADDI_GIPKL_BS":
			session_selected_scenes.add(new Scene("D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_GIKPL_2025\\Scenes\\BS.sum",KabaddiUtil.ONE)); 
			session_selected_scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In START ;");
			TimeUnit.SECONDS.sleep(5);
			this_GIPKL = new KABADDI_GIPKL();
			this_GIPKL.scorebug = new ScoreBug();
			break;
		case "KABADDI_GIPKL":
			session_selected_scenes.add(new Scene("",KabaddiUtil.ONE)); 
			session_selected_scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
			this_GIPKL = new KABADDI_GIPKL();
			this_GIPKL.scorebug = new ScoreBug();
			break;
		
		case "KABADDI_GIPKL_AR":
			session_selected_scenes.add(new Scene("D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_GIKPL_2025\\Scenes\\BS.sum",KabaddiUtil.ONE)); 
			session_selected_scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
			break;
		case KabaddiUtil.KABADDI:
			session_selected_scenes.add(new Scene("D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_UPKL_2024\\Scenes\\Big_Screen.sum",KabaddiUtil.ONE)); // Front layer
			//session_selected_scenes.add(new Scene(KabaddiUtil.BG_SCENE_PATH,KabaddiUtil.THREE));
			session_selected_scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectLogo_Data 0 ;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In START;");
			//session_selected_scenes.get(1).scene_load(print_writer, session_selected_broadcaster);
			this_Kabaddi = new KABADDI();
			this_Kabaddi.scorebug = new ScoreBug();
			break;
		
		}
		//
		
//		JAXBContext.newInstance(Configurations.class).createMarshaller().marshal(session_Configurations, 
//			new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY + KabaddiUtil.BIGSCREEN_XML));
		model.addAttribute("session_Configurations", session_Configurations);
		model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);		
		return "match";
	}
	
	@RequestMapping(value = {"/processKabaddiProcedures"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processKabaddiProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)
					throws Exception
	{	
		switch (whatToProcess.toUpperCase()) {
		case "GET-CONFIG-DATA":

			session_Configurations = (Configurations)JAXBContext.newInstance(Configurations.class).createUnmarshaller().unmarshal(
				new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CONFIGURATIONS_DIRECTORY + valueToProcess));
			
			return JSONObject.fromObject(session_Configurations).toString();
			
		case "SWAP_DATA":
			if(session_selected_broadcaster.toUpperCase().equalsIgnoreCase("KABADDI")) {
				if(this_Kabaddi.scorebug.isValue_is_swap() == false) {
					SwapMatch = KabaddiFunctions.SwapMatch(session_match);
					this_Kabaddi.scorebug.setValue_is_swap(true);
				}else if(this_Kabaddi.scorebug.isValue_is_swap() == true) {
					SwapMatch = session_match;
					this_Kabaddi.scorebug.setValue_is_swap(false);
				}

			}
			return JSONObject.fromObject(session_match).toString();
			
		case "MATCH-PROMO_GRAPHICS-OPTIONS": 
			return JSONArray.fromObject(KabaddiFunctions.processAllFixtures(kabaddiService)).toString();
		case "EXCEL_FF_GRAPHICS_OPTION":
				return JSONArray.fromObject(KabaddiFunctions.ReadExcel(KabaddiUtil.FF_EXCEL).keySet()).toString();	
		
		case "RE_CONNECT":
			session_socket = new Socket(session_Configurations.getIpAddress(), Integer.valueOf(session_Configurations.getPortNumber()));
			print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			return JSONObject.fromObject(session_match).toString();
			
		case KabaddiUtil.LOAD_MATCH:
			objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
			
			session_event = new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.EVENT_DIRECTORY +
					valueToProcess), EventFile.class);
			session_match = new ObjectMapper().readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY +
					valueToProcess), Match.class);
			session_match.setEvents(session_event.getEvents());
			
			session_match = KabaddiFunctions.populateMatchVariables(kabaddiService, new ObjectMapper().readValue (new File(KabaddiUtil.KABADDI_DIRECTORY + 
					KabaddiUtil.MATCHES_DIRECTORY + valueToProcess),Match.class));
			
			switch (session_selected_broadcaster) {
			case "KABADDI_GIPKL":case "KABADDI_GIPKL_AR":case "KABADDI_GIPKL_BS":
				session_match.getApi_Match().setHomeTeam(session_match.getHomeTeam());
				session_match.getApi_Match().setAwayTeam(session_match.getAwayTeam());
				
				KabaddiFunctions.setMatch(session_match.getApi_Match(), session_match);
				
				KabaddiFunctions.setPreMatch(api_pre_match, session_match);
				
				session_match.setApi_PreMatch(new ArrayList<Api_pre_match>());
				session_match.getApi_PreMatch().add(api_pre_match);
				
				break;

			default:
				if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() + "-in-match" + KabaddiUtil.JSON_EXTENSION).exists()) {
				session_match.setApi_Match(objectMapper.readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() 
					+ "-in-match" + KabaddiUtil.JSON_EXTENSION), Api_Match.class));
				}
				
				if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + "pre-match_2m" + KabaddiUtil.JSON_EXTENSION).exists()) {
					try {
			            String filePath = KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + "pre-match_2m" + KabaddiUtil.JSON_EXTENSION;
			            File file = new File(filePath);
			            
			            List<Api_pre_match> apiPreMatchList = objectMapper.readValue(file, new TypeReference<List<Api_pre_match>>() {});
	
			            session_match.setApi_PreMatch(apiPreMatchList);
	
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
				}
			
				break;
			}
			if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON).exists()) {
				if(session_match != null) {
					session_match.setClock(objectMapper.readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + 
							KabaddiUtil.CLOCK_JSON), Clock.class));
				}
			} else {
				session_match.setClock(new Clock());
			}
			if(session_selected_broadcaster.toUpperCase().equalsIgnoreCase("KABADDI_GIPKL")) {
				this_GIPKL.scorebug.setValue_is_swap(false);

			}else if(session_selected_broadcaster.toUpperCase().equalsIgnoreCase("KABADDI")) {
				this_Kabaddi.scorebug.setValue_is_swap(false);
			}
			return JSONObject.fromObject(session_match).toString();

		case "READ-MATCH-AND-POPULATE":
			
			try {
				counter++;
				
				if(counter == 15) {
					
					if(session_Configurations.getPortNumber() != 0) {
						session_socket = new Socket(session_Configurations.getIpAddress(), Integer.valueOf(session_Configurations.getPortNumber()));
						print_writer = new PrintWriter(session_socket.getOutputStream(), true);
					}
					counter = 0;
				}
				
				if(session_match != null && !valueToProcess.equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
						new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).lastModified())))
				{
					session_match = KabaddiFunctions.populateMatchVariables(kabaddiService, new ObjectMapper().readValue
							(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()),
					        Match.class));	
					
					session_match.setMatchFileTimeStamp(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
							new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.MATCHES_DIRECTORY + session_match.getMatchFileName()).lastModified()));
					
				}
				
				if(session_match != null && session_selected_broadcaster != null) {
					if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON).exists()) {
				        try {
				        	if(session_match != null) {
					        	session_match.setClock(objectMapper.readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.CLOCK_JSON), Clock.class));
				        	}
				        } catch (MismatchedInputException e) {
				        	e.printStackTrace();
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
					}
					switch (session_selected_broadcaster) {
					case "KABADDI_GIPKL":	case "KABADDI_GIPKL_AR":case "KABADDI_GIPKL_BS":
						KabaddiFunctions.setMatch(session_match.getApi_Match(), session_match);
						break;

					default:
						if(session_match != null) {
							if(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() + "-in-match" + KabaddiUtil.JSON_EXTENSION).exists()) {
								try {
									session_match.setApi_Match(objectMapper.readValue(new File(KabaddiUtil.KABADDI_DIRECTORY + KabaddiUtil.DESTINATION_DIRECTORY + session_match.getMatchId() 
									+ "-in-match" + KabaddiUtil.JSON_EXTENSION), Api_Match.class));
						        } catch (MismatchedInputException e) {
						        	e.printStackTrace();
						        } catch (IOException e) {
						            e.printStackTrace();
						        }
							}
						}
						
						break;
					}
					
					if(session_selected_broadcaster.toUpperCase().equalsIgnoreCase("KABADDI")) {
						if(this_Kabaddi.scorebug.isValue_is_swap() == false) {
							SwapMatch = session_match;
						}else if(this_Kabaddi.scorebug.isValue_is_swap() == true) {
							SwapMatch = KabaddiFunctions.SwapMatch(session_match);
						}
					}
					if(session_selected_broadcaster != null) {
						switch (session_selected_broadcaster) {
						case KabaddiUtil.KABADDI:
							this_Kabaddi.updateScoreBug(session_selected_scenes, session_match, SwapMatch, print_writer);
							break;
						case "KABADDI_GIPKL":
							this_GIPKL.updateScoreBug(session_selected_scenes, session_match, SwapMatch, print_writer);
							break;
						}
					}
				}
			} catch (Exception e) {
	            // Handle specific exceptions
	            e.printStackTrace(); // Print stack trace for server-side debugging
	            
	            JSONObject errorResponse = new JSONObject();
	            errorResponse.put("error", "Error processing request: " + e.getMessage());
	            return errorResponse.toString();
			}
			
			return JSONObject.fromObject(session_match).toString();			
		default:
			
			switch (session_selected_broadcaster) {
			case KabaddiUtil.KABADDI:
				this_Kabaddi.ProcessGraphicOption(whatToProcess, session_match, SwapMatch, kabaddiService, print_writer, session_selected_scenes, valueToProcess);
				break;
			case "KABADDI_GIPKL":case "KABADDI_GIPKL_BS":
				this_GIPKL.ProcessGraphicOption(whatToProcess, session_match, SwapMatch, kabaddiService, print_writer, session_selected_scenes, valueToProcess);
				break;
			case "KABADDI_GIPKL_AR":
				this_GIPKL_AR.ProcessGraphicOption(whatToProcess, session_match, SwapMatch, kabaddiService, print_writer, session_selected_scenes, valueToProcess);
				break;
			}
			return JSONObject.fromObject(session_match).toString();
		}
	}
}