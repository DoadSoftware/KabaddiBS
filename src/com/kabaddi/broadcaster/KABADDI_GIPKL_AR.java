package com.kabaddi.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import com.kabaddi.model.Fixture;
import com.kabaddi.model.Ground;
import com.kabaddi.model.Match;
import com.kabaddi.model.Player;
import com.kabaddi.model.PlayerStats;
import com.kabaddi.model.Team;
import com.kabaddi.service.KabaddiService;
import com.kabaddi.util.KabaddiFunctions;
import com.kabaddi.util.KabaddiUtil;

import net.sf.json.JSONArray;

import com.healthmarketscience.jackcess.Index;
import com.kabaddi.containers.Scene;
import com.kabaddi.containers.ScoreBug;
import com.kabaddi.controller.IndexController;

public class KABADDI_GIPKL_AR extends Scene {

	public String session_selected_broadcaster = "KABADDI_GIPKL_AR";

	public ScoreBug scorebug = new ScoreBug();
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false,is_home_raider_in = false,is_away_raider_in = false;
	public int team_id=0;
	public long last_date = 0;
	public int Whichside = 2;
	public String logo_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_GIKPL_2025\\Logos\\";
	
	public KABADDI_GIPKL_AR() {
		super();
	}

	public ScoreBug updateScoreBug(List<Scene> scenes, Match match, Match swapMatch, PrintWriter print_writer)throws InterruptedException, MalformedURLException, IOException {
		
		
		return scorebug;
	}
	
	public Object ProcessGraphicOption(String whatToProcess, Match match, Match swapMatch, KabaddiService kabaddiService,PrintWriter print_writer, List<Scene> scenes, String valueToProcess)
			throws InterruptedException, NumberFormatException, MalformedURLException, IOException, JAXBException {

		switch (whatToProcess.toUpperCase()) {
		//ScoreBug
		case "POPULATE-MATCH_ID": case "POPULATE-RAID": case "POPULATE-DO_DIE": case "POPULATE-BONUS2":
		case "POPULATE-SECOND_DO_DIE": case "POPULATE-SUPER": case "POPULATE-TACKLE": case "POPULATE-BONUS":
		case "POPULATE-CHROMA": case "POPULATE-SECOND_BONUS": case "POPULATE-REVERSE_MATCH_ID":
			
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-MATCH_ID": case "POPULATE-RAID": case "POPULATE-DO_DIE":
		case "POPULATE-SECOND_DO_DIE": case "POPULATE-SUPER": case "POPULATE-TACKLE": case "POPULATE-BONUS":
		case "POPULATE-SECOND_BONUS": case "POPULATE-REVERSE_MATCH_ID": case "POPULATE-BONUS2":
			
			scenes.get(0).setScene_path(valueToProcess.split(",")[0]);
			scenes.get(0).scene_load(session_selected_broadcaster,print_writer);
			
			break;
		}
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-CHROMA":
			populateChroma(print_writer,valueToProcess, match, session_selected_broadcaster);
			break;
		case "POPULATE-MATCH_ID":
			populateMatchId(print_writer, match, session_selected_broadcaster);
			break;
		case "POPULATE-BONUS": case "POPULATE-SECOND_BONUS": case "POPULATE-BONUS2":
			populateBonus(print_writer, match, session_selected_broadcaster);
			break;
		case "POPULATE-RAID":
			populateRaid(print_writer, valueToProcess, match, session_selected_broadcaster);
			break;
		case "POPULATE-DO_DIE":	
			populateDoDie(print_writer, Integer.valueOf(valueToProcess.split(",")[1])
					, match, session_selected_broadcaster,kabaddiService);
			break;
		case "POPULATE-SECOND_DO_DIE":
			populateSecondDoDie(print_writer, Integer.valueOf(valueToProcess.split(",")[1])
					, match, session_selected_broadcaster,kabaddiService);
			break;
		case "POPULATE-SUPER":
			populateSuper(print_writer, Integer.valueOf(valueToProcess.split(",")[1])
					, match, session_selected_broadcaster,kabaddiService);
			break;
		case "POPULATE-TACKLE":
			populateTackle(print_writer, Integer.valueOf(valueToProcess.split(",")[1])
					, match, session_selected_broadcaster,kabaddiService);
			break;
		}
		
		case "CLEAR-ALL":
		case "ANIMATE-IN-MATCH_ID":	case "ANIMATE-OUT": case "ANIMATE-IN-DO_DIE": case "ANIMATE-IN-SECOND_DO_DIE": case "ANIMATE-IN-SUPER":
		case "ANIMATE-IN-TACKLE": case "ANIMATE-IN-BONUS": case "ANIMATE-IN-SECOND_BONUS": case "ANIMATE-IN-BONUS2":
			switch (whatToProcess.toUpperCase()) {

			case "ANIMATE-IN-MATCH_ID":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,3);
				which_graphics_onscreen = "MATCH_ID";
				break;
			case "ANIMATE-IN-DO_DIE":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,3);
				which_graphics_onscreen = "DO_DIE";
				break;
			case "ANIMATE-IN-SECOND_DO_DIE":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,3);
				which_graphics_onscreen = "SECOND_DO_DIE";
				break;
			case "ANIMATE-IN-SUPER":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,3);
				which_graphics_onscreen = "SUPER";
				break;
			case "ANIMATE-IN-TACKLE":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,3);
				which_graphics_onscreen = "TACKLE";
				break;
			case "ANIMATE-IN-BONUS": case "ANIMATE-IN-BONUS2":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,3);
				which_graphics_onscreen = "BONUS";
				break;
			case "ANIMATE-IN-SECOND_BONUS":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,3);
				which_graphics_onscreen = "SECOND_BONUS";
				break;
			case "CLEAR-ALL":
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE CLEAR;");
				print_writer.println("LAYER3*EVEREST*SINGLE_SCENE CLEAR;");
				which_graphics_onscreen = "";
				break;
			case "ANIMATE-OUT":
				switch (which_graphics_onscreen.toUpperCase()) {
					case "MATCH_ID": case "DO_DIE": case "SECOND_DO_DIE": case "SUPER": case "TACKLE":
					case "BONUS": case "SECOND_BONUS":
						processAnimation(print_writer, "Out", "START", session_selected_broadcaster,3);
						which_graphics_onscreen = "";
						break;
				}
				break;
			}
			break;
		}
		return null;
	}

	

	public void processAnimation(PrintWriter print_writer, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		switch(which_broadcaster.toUpperCase()) {
		case "KABADDI_GIPKL_AR":
			switch(which_layer) {
			case 1:
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 2:
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 3:
				print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			}
			break;
		}
	}
	
	public void populateMatchId(PrintWriter print_writer, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: Match -> Match is null");
		} else {
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead " + match.getMatchIdent().toUpperCase() + ";");
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + match.getHomeTeam().getTeamName2() + "\n" 
					+ match.getHomeTeam().getTeamName3() + ";");

			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayTeamName " + match.getAwayTeam().getTeamName2() + "\n" 
					+ match.getAwayTeam().getTeamName3() + ";");

			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeam " + logo_path + 
					match.getHomeTeam().getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeam " + logo_path + 
					match.getAwayTeam().getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
	public void populateChroma(PrintWriter print_writer,String data, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		print_writer.println("LAYER1*EVEREST*FRONT_IMAGE SAVED_CHROMA " + data + ";");
	}
	public void populateBonus(PrintWriter print_writer, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: Match -> Match is null");
		} else {
			
//			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHead " + match.getMatchIdent().toUpperCase() + ";");

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
	public void populateRaid(PrintWriter print_writer,String viz_sence_path, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: Match -> Match is null");
		} else {
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeam " + logo_path + 
					match.getHomeTeam().getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");

			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
	public void populateDoDie(PrintWriter print_writer,int teamID, Match match, String selectedbroadcaster,KabaddiService kabaddiService) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: Match -> Match is null");
		} else {
			
			Team team = kabaddiService.getTeams().stream().filter(tm -> tm.getTeamId() == teamID).findAny().orElse(null);
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeam " + logo_path + 
					team.getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + "DO" + "\n" + "OR DIE" + ";");

			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
	public void populateSecondDoDie(PrintWriter print_writer,int teamID, Match match, String selectedbroadcaster,KabaddiService kabaddiService) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: Match -> Match is null");
		} else {
			
			Team team = kabaddiService.getTeams().stream().filter(tm -> tm.getTeamId() == teamID).findAny().orElse(null);
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeam " + logo_path + 
					team.getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + "DO" + "\n" + "OR DIE" + ";");

			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
	public void populateSuper(PrintWriter print_writer,int teamID, Match match, String selectedbroadcaster,KabaddiService kabaddiService) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: Match -> Match is null");
		} else {
			
			Team team = kabaddiService.getTeams().stream().filter(tm -> tm.getTeamId() == teamID).findAny().orElse(null);
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeam " + logo_path + 
					team.getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + "SUPER" + "\n" + "TACKLE ON" + ";");

			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
	public void populateTackle(PrintWriter print_writer,int teamID, Match match, String selectedbroadcaster,KabaddiService kabaddiService) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: Match -> Match is null");
		} else {
			
			Team team = kabaddiService.getTeams().stream().filter(tm -> tm.getTeamId() == teamID).findAny().orElse(null);
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeam " + logo_path + 
					team.getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			
			print_writer.println("LAYER3*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeTeamName " + "SUPER" + "\n" + "TACKLE ON" + ";");

			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
}
