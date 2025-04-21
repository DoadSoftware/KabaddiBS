package com.kabaddi.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import com.kabaddi.model.Fixture;
import com.kabaddi.model.Ground;
import com.kabaddi.model.Match;
import com.kabaddi.model.NameSuper;
import com.kabaddi.model.Player;
import com.kabaddi.model.PlayerComparison;
import com.kabaddi.model.PlayerStats;
import com.kabaddi.model.Team;
import com.kabaddi.service.KabaddiService;
import com.kabaddi.util.KabaddiFunctions;
import com.kabaddi.util.KabaddiUtil;

import javassist.expr.NewArray;
import net.sf.json.JSONArray;

import com.healthmarketscience.jackcess.Index;
import com.kabaddi.containers.Scene;
import com.kabaddi.containers.ScoreBug;
import com.kabaddi.controller.IndexController;

public class KABADDI_GIPKL extends Scene {

	public String session_selected_broadcaster = "KABADDI_GIPKL";

	public ScoreBug scorebug = new ScoreBug();
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false,is_home_raider_in = false,is_away_raider_in = false;
	public int team_id=0;
	public long last_date = 0;
	public int Whichside = 2;
	public String logo_path = "D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_GIKPL_2025\\Logos\\";
	public String photo_path = "C:\\Images\\GIPKL\\Photos\\";

	public KABADDI_GIPKL() {
		super();
	}

	public ScoreBug updateScoreBug(List<Scene> scenes, Match match, Match swapMatch, PrintWriter print_writer)throws InterruptedException, MalformedURLException, IOException {
		
		if(which_graphics_onscreen.equalsIgnoreCase("SCOREBUG")) {
			scorebug = populateScoreBug(true, scorebug, print_writer, swapMatch,session_selected_broadcaster);
		}else if(which_graphics_onscreen.equalsIgnoreCase("SCORELINE")) {
			scorebug = populateScoreLine(true, scorebug, print_writer, match,session_selected_broadcaster);
		}
		return scorebug;
	}
	public ScoreBug updateScoreBug_Manual(List<Scene> scenes, Match match, Match swapMatch, PrintWriter print_writer)throws InterruptedException, MalformedURLException, IOException {
		
		if(which_graphics_onscreen.equalsIgnoreCase("MANUAL_SCOREBUG")) {
			scorebug = populateScoreBug_Manual(true, scorebug, print_writer, swapMatch,session_selected_broadcaster);
		}
		return scorebug;
	}
	public Object ProcessGraphicOption(String whatToProcess, Match match, Match swapMatch, KabaddiService kabaddiService,PrintWriter print_writer, List<Scene> scenes, String valueToProcess)
			throws Exception {

		switch (whatToProcess.toUpperCase()) {
		//ScoreBug
		
		case "POPULATE-SCOREBUG": case "POPULATE-SCORELINE": case "POPULATE-TOURNAMENT_LOGO": case "POPULATE-GOLDEN_RAID":	case "POPULATE-L3-NAMESUPER":
		case "POPULATE-FF_MATCH_ID":case "POPULATE-FF_TOURNAMENT ROULES":case "POPULATE-FF_MATCH_PROMO":case "POPULATE-FF_GRAPHICS":case "POPULATE-MANUAL_SCOREBUG":
		case "POPULATE-L3-GRAPHICS":case "POPULATE-L3-FIXTURES":case "POPULATE-FF-PLAYER_COMPARE":
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-SCORELINE": case "POPULATE-TOURNAMENT_LOGO": case "POPULATE-GOLDEN_RAID":
			scenes.get(0).setScene_path("D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_GIKPL_2025\\Scenes\\BS.sum");
			scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
			break;
		case "POPULATE-FF_MATCH_ID":case "POPULATE-FF_TOURNAMENT ROULES":case "POPULATE-FF_MATCH_PROMO":case "POPULATE-FF_GRAPHICS":
		case "POPULATE-L3-NAMESUPER":case "POPULATE-L3-GRAPHICS":case "POPULATE-L3-FIXTURES":case "POPULATE-FF-PLAYER_COMPARE":
			scenes.get(0).setScene_path(valueToProcess.split(",")[0]);
			scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
			break;
		case "POPULATE-MANUAL_SCOREBUG":
			scenes.get(0).setScene_path("D:\\DOAD_In_House_Everest\\Everest_Sports\\Everest_GIKPL_2025\\Scenes\\SB_Mannual.sum");
			scenes.get(0).scene_load(print_writer, session_selected_broadcaster);
			break;
		}
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG":
			populateScoreBug(false, scorebug, print_writer, swapMatch,session_selected_broadcaster);
			break;
		case "POPULATE-SCORELINE":
			populateScoreLine(false, scorebug, print_writer, match,session_selected_broadcaster);
			break;
		case "POPULATE-TOURNAMENT_LOGO":
			populateTournamentLogo(false, scorebug, print_writer, match, session_selected_broadcaster);
			break;
		case "POPULATE-GOLDEN_RAID":
			populateGoldenRaid(false, scorebug, print_writer, match, session_selected_broadcaster);
			break;
		case "POPULATE-FF_MATCH_ID":
			populateMatchId(print_writer, match, session_selected_broadcaster);
			break;
		case "POPULATE-FF_MATCH_PROMO":
			Fixture fixture = KabaddiFunctions.processAllFixtures(kabaddiService).stream().filter(fix->fix.getMatchnumber()==
					Integer.valueOf(valueToProcess.split(",")[1])).findAny().orElse(null);
			
			Ground Ground = kabaddiService.getGround(fixture.getVenue());
			populateMatchPromo(print_writer, match,fixture,Ground, session_selected_broadcaster);
			break;
		case "POPULATE-FF_GRAPHICS":
			populateFF_EXCEL(whatToProcess,valueToProcess.substring(valueToProcess.lastIndexOf(",")+1),print_writer,kabaddiService.getAllPlayer());
			break;
		case "POPULATE-L3-GRAPHICS":
			populateLT_EXCEL(whatToProcess,valueToProcess.substring(valueToProcess.lastIndexOf(",")+1),print_writer,kabaddiService.getAllPlayer());
			break;
		case "POPULATE-L3-NAMESUPER":
			for(NameSuper ns : kabaddiService.getNameSupers()) {
			  if(ns.getNamesuperId() == Integer.valueOf(valueToProcess.split(",")[1])) {
				  populateNameSuper(print_writer, valueToProcess.split(",")[0], ns, match, session_selected_broadcaster);
			  }
			}
			break;
		case "POPULATE-MANUAL_SCOREBUG":
			populateScoreBug_Manual(false, scorebug, print_writer, swapMatch, valueToProcess);
			break;
		case "POPULATE-L3-FIXTURES":
			populateL3Fixtures(print_writer, KabaddiFunctions.processAllFixtures(kabaddiService),match,
					Integer.valueOf(valueToProcess.split(",")[valueToProcess.split(",").length - 2]),
					valueToProcess.substring(valueToProcess.lastIndexOf(",")+1));
			break;
		case "POPULATE-FF-PLAYER_COMPARE":
			populateFFPlayerComaprison(print_writer, KabaddiFunctions.processAllPlayerStatsComparion(kabaddiService),match,
					valueToProcess.substring(valueToProcess.lastIndexOf(",")+1),kabaddiService);
			break;
		case "POPULATE-FF_TOURNAMENT ROULES":
			  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 240.0;");
			  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			  print_writer.
			  println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			  print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			  TimeUnit.SECONDS.sleep(1);
			  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
			break;
		}
			
		case "ANIMATE-IN-SCOREBUG": case "CLEAR-ALL": case "ANIMATE-OUT-SCOREBUG": case "RESET-ALL-ANIMATION":case "ANIMATE-IN-FF-PLAYER_COMPARE":
		case "ANIMATE-IN-SCORELINE": case "ANIMATE-OUT": case "ANIMATE-IN-TOURNAMENT_LOGO": case "ANIMATE-IN-GOLDEN_RAID":
		case "ANIMATE-IN-FF_MATCH_ID":case "ANIMATE-IN-FF_TOURNAMENT ROULES":case "ANIMATE-IN-FF_MATCH_PROMO":case "ANIMATE-IN-FF_FF_GRAPHICS":
		case "ANIMATE-IN-L3-NAMESUPER":case "ANIMATE-IN-MANUAL_SCOREBUG":case "ANIMATE-IN-LT_GRAPHICS":case "ANIMATE-IN-L3-FIXTURES":
			switch (whatToProcess.toUpperCase()) {

			case "ANIMATE-IN-SCOREBUG":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				TimeUnit.SECONDS.sleep(2);
				processAnimation(print_writer, "In", "COUNTINUE", session_selected_broadcaster,1);
				is_infobar = true;
				which_graphics_onscreen = "SCOREBUG";
				break;
			case "ANIMATE-IN-SCORELINE": 
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				is_infobar = true;
				which_graphics_onscreen = "SCORELINE";
				break;
			case "ANIMATE-IN-TOURNAMENT_LOGO":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				is_infobar = true;
				which_graphics_onscreen = "TOURNAMENT_LOGO";
				break;
			case "ANIMATE-IN-GOLDEN_RAID":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				is_infobar = true;
				which_graphics_onscreen = "GOLDEN_RAID";
				break;
			case "CLEAR-ALL":
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE CLEAR;");
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE CLEAR;");
				print_writer.println("LAYER3*EVEREST*SINGLE_SCENE CLEAR;");
				which_graphics_onscreen = "";
				is_infobar = false;
				break;
			case "ANIMATE-OUT-SCOREBUG":
				processAnimation(print_writer, "Out", "START", session_selected_broadcaster,1);
				is_infobar = false;
				which_graphics_onscreen="";
				break;
			case "ANIMATE-OUT":
				processAnimation(print_writer, "Out", "START", session_selected_broadcaster,1);
				TimeUnit.SECONDS.sleep(1);
				which_graphics_onscreen="";
				break;
			case "RESET-ALL-ANIMATION":
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tAwayRaiderClock TIMER_OFFSET 30;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tHomeRaiderClock TIMER_OFFSET 30;");
				break;
			case "ANIMATE-IN-FF_MATCH_ID": case "ANIMATE-IN-FF_TOURNAMENT ROULES":case "ANIMATE-IN-FF_MATCH_PROMO":case "ANIMATE-IN-FF-PLAYER_COMPARE":
			case "ANIMATE-IN-FF_FF_GRAPHICS":case "ANIMATE-IN-L3-NAMESUPER":case "ANIMATE-IN-LT_GRAPHICS":case "ANIMATE-IN-L3-FIXTURES":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "MATCH_ID";
				break;
			case "ANIMATE-IN-MANUAL_SCOREBUG":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
				which_graphics_onscreen = "MANUAL_SCOREBUG";
				break;
			}
			break;
		}
		return null;
	}

	
	private void populateFFPlayerComaprison(PrintWriter print_writer,List<PlayerComparison> AllPlayerStats, Match match, String id, KabaddiService kabaddiService) {
		PlayerComparison PlayerComparison = AllPlayerStats.stream().filter(ps -> ps.getPlayerStatsId() 
				== Integer.valueOf(id)).findAny().orElse(null);
		
		List<String> data_str = new ArrayList<String>();
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader "+ PlayerComparison.getHeader() +";");
        
        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHead "+ PlayerComparison.getSubHeader() +";");
        
			data_str.add(PlayerComparison.getPlayer1Value1()+","+ PlayerComparison.getHeadStats1() +","+ PlayerComparison.getPlayer2Value1());
			data_str.add(PlayerComparison.getPlayer1Value2()+","+ PlayerComparison.getHeadStats2() +","+ PlayerComparison.getPlayer2Value2());
			data_str.add(PlayerComparison.getPlayer1Value3()+","+ PlayerComparison.getHeadStats3() +","+ PlayerComparison.getPlayer2Value3());
			data_str.add(PlayerComparison.getPlayer1Value4()+","+ PlayerComparison.getHeadStats4() +","+ PlayerComparison.getPlayer2Value4());
			data_str.add(PlayerComparison.getPlayer1Value5()+","+ PlayerComparison.getHeadStats5() +","+ PlayerComparison.getPlayer2Value5());

	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgImage01 "+ photo_path +"\\"+PlayerComparison.getTeam().getTeamBadge()+"\\"
	        		+ PlayerComparison.getPlayer().getPhoto() + KabaddiUtil.PNG_EXTENSION  +";");
	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgImage02 "+ photo_path +"\\"+PlayerComparison.getTeam1().getTeamBadge()+"\\"
	        		+ PlayerComparison.getPlayer1().getPhoto() + KabaddiUtil.PNG_EXTENSION  +";");
	        
	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName01 "+ PlayerComparison.getPlayer().getFull_name() +";");
	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam01 "+ PlayerComparison.getTeam().getTeamName1() +";");
	        
	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tName02 "+ PlayerComparison.getPlayer1().getFull_name() +";");
	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTeam02 "+ PlayerComparison.getTeam1().getTeamName1() +";");


		for(int i=0;i<data_str.size();i++) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0"  + (i+1)+" "+ data_str.get(i).split(",")[0] +";");
	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead0"  + (i+1)+" "+ data_str.get(i).split(",")[1] +";");
	        print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" +(i+1)+" "+ data_str.get(i).split(",")[2] +";");
   
		}
        
	}

	public void processAnimation(PrintWriter print_writer, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		switch(which_broadcaster.toUpperCase()) {
		case "KABADDI_GIPKL":
			switch(which_layer) {
			case 1:
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			case 2:
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*" + animationName + " " + animationCommand + ";");
				break;
			}
			break;
		}
	}
	private void populateL3Fixtures(PrintWriter printWriter, List<Fixture> AllFixtures, Match match, int teamId, String TeamName) throws Exception {
		
		List<Fixture> fix = new ArrayList<Fixture>();
		
		printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFirstName "+ TeamName +";");
        
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tLastName ;");
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubInfo NEXT 3 FIXTURES;");
        //HEADER
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead01 " + ";");
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead02 " + ";");
        
        int count = 0;
        for (Fixture fx : AllFixtures) {
            try {
                if (new SimpleDateFormat("dd-MM-yyyy").parse(fx.getDate()).compareTo(new Date()) >= 0 &&
                    (fx.getHometeamid() == teamId || fx.getAwayteamid() == teamId)) {
                    fix.add(fx);
                    if (++count == 3) break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

     // Body of table
        boolean flag = false;
        for(int i=0; i <fix.size(); i++) {
        	 printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + (i+1) + "A " + 
        			 " vs "+(fix.get(i).getAway_Team().getTeamName1().equalsIgnoreCase(TeamName) ? 
        			 fix.get(i).getHome_Team().getTeamName1():fix.get(i).getAway_Team().getTeamName1())+ ";");
        	 
             printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + (i+1) + "B " 
        			 + KabaddiFunctions.ordinal(Integer.valueOf(fix.get(i).getDate().split("-")[0].replaceFirst("0", ""))) + " " + 
						Month.of(Integer.valueOf(fix.get(i).getDate().split("-")[1]))+ " " +
						fix.get(i).getDate().substring(fix.get(i).getDate().lastIndexOf("-")+1)+ ";");
             if(!flag) {
            	 printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_HomeLogo " + logo_path
     					+ (fix.get(i).getAway_Team().getTeamName1().equalsIgnoreCase(TeamName) ? 
     		        	fix.get(i).getAway_Team().getTeamBadge():fix.get(i).getHome_Team().getTeamBadge()) + KabaddiUtil.PNG_EXTENSION + ";");
            	 flag = true;
             }
        }
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectRows 2 ;");
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectColumm "+(fix.size()-1)+" ;");
        
	  printWriter.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 160.0;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
	  printWriter.
	  println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
	  printWriter.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
	  TimeUnit.SECONDS.sleep(1);
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
	  printWriter.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
	}

	public void populateMatchId(PrintWriter print_writer, Match match,String session_selected_broadcaster) throws InterruptedException {
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader "
				+ match.getMatchIdent().toUpperCase()+ ";");

		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_HomeLogo " + logo_path
				+ match.getHomeTeam().getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
		
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_AwayLogo " + logo_path
				+ match.getAwayTeam().getTeamBadge().toUpperCase()+ KabaddiUtil.PNG_EXTENSION + ";");
		
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHead " + "LIVE FROM "
				+ match.getVenueName().toUpperCase() + ";");
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo ;");

		  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 240.0;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		  print_writer.
		  println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
		  print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
		  TimeUnit.SECONDS.sleep(1);
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
		  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		 

	}
	
	
	private void populateLT_EXCEL(String whatToProcess, String ValueToProcess, PrintWriter printWriter,
			List<Player> allPlayer) throws InterruptedException {
		 Map<String, Object> rowData = KabaddiFunctions.ReadExcel("C:\\Sports\\Kabaddi\\LT_EVERSET_EXCEL.xlsx").get(ValueToProcess);

	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFirstName " + 
	    	        (rowData.get("HEADER") != null ? rowData.get("HEADER").toString().trim() : "") + ";");
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tLastName ;");
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubInfo " +  (rowData.get("SUB HEADER") != null ? rowData.get("SUB HEADER").toString().trim() : "") + ";");
	        
			int cols = rowData.get("COLS") != null ? (rowData.get("COLS") instanceof Number ? ((Number) rowData.get("COLS")).intValue() : Integer.parseInt(rowData.get("COLS").toString().trim())) : 0;
	        int rows = rowData.get("ROWS") != null ? (rowData.get("ROWS") instanceof Number ? ((Number) rowData.get("ROWS")).intValue() : Integer.parseInt(rowData.get("ROWS").toString().trim())) : 0;
	        
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectRows " + Math.abs(cols) + " ;");
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectColumm " + (rows-1) + " ;");

	        // Header of table
	        for (int j = 1; j <= cols; j++) {
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead0" + j + " " + 
	            		(rowData.get("H" + j) != null ? rowData.get("H" + j).toString().trim() : "") + ";");
	        }

	        // Body of table
	        for (int i = 1; i <= rows; i++) {
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "A " + (rowData.get("R" + i + ".1") != null ? rowData.get("R" + i + ".1").toString().trim() : "") + ";");
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "B " + (rowData.get("R" + i + ".2") != null ? rowData.get("R" + i + ".2").toString().trim() : "") + ";");
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "C " + (rowData.get("R" + i + ".3") != null ? rowData.get("R" + i + ".3").toString().trim() : "") + ";");
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "D " + (rowData.get("R" + i + ".4") != null ? rowData.get("R" + i + ".4").toString().trim() : "") + ";");
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "E " + (rowData.get("R" + i + ".5") != null ? rowData.get("R" + i + ".5").toString().trim() : "") + ";");

	        }

	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_HomeLogo " + logo_path
					+ (rowData.get("LOGO") != null ? rowData.get("LOGO").toString().trim() : "Tlogo") + KabaddiUtil.PNG_EXTENSION + ";");
			 
		  printWriter.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
		  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
		  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
		  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 160.0;");
		  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		  printWriter.
		  println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
		  printWriter.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
		  TimeUnit.SECONDS.sleep(1);
		  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
		  printWriter.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		
	}
	public void populateNameSuper(PrintWriter print_writer, String viz_scene, NameSuper ns, Match match,
			String session_selected_broadcaster) throws InterruptedException {
		
				if(ns.getSponsor() == null) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_HomeLogo " + logo_path
							+ "Tlogo" + KabaddiUtil.PNG_EXTENSION + ";");
				}else {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_HomeLogo " + logo_path
							+ "Sponsor//" + ns.getSponsor() + KabaddiUtil.PNG_EXTENSION + ";");
				}
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tFirstName "
						+ (ns.getFirstname()==null ?"":ns.getFirstname())+ ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tLastName "
						+ (ns.getSurname()==null ?"":ns.getSurname()) + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubInfo "
						+ ns.getSubLine().toUpperCase() + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo  ;");

				  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
				  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
				  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
				  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 240.0;");
				  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
				  print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
				  print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
				  TimeUnit.SECONDS.sleep(1);
				  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
				  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
			
	}
	private void populateMatchPromo(PrintWriter print_writer, Match match, Fixture fixture, Ground ground,
			String session_selected_broadcaster) throws InterruptedException {
		
		String match_name="";
		
		if(fixture.getMatchnumber() < 10) {
			match_name = "MATCH " + fixture.getMatchnumber();
		}else {
			match_name = fixture.getMatchfilename().toUpperCase();
		}
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader "
				+ match_name + ";");

		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_HomeLogo " + logo_path
				+ fixture.getHome_Team().getTeamBadge().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
		
		print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_AwayLogo " + logo_path
				+ fixture.getAway_Team().getTeamBadge().toUpperCase()+ KabaddiUtil.PNG_EXTENSION + ";");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, +1);
		if(fixture.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()))) {
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHead " + "TOMORROW - " + fixture.getTime() +" IST "+ ";");	
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo FROM " + 
					ground.getFullname()+ ";");

		}else {
			cal.add(Calendar.DATE, -1);
			if(fixture.getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()))) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHead " + "UP NEXT - " + fixture.getTime() +" IST "+ ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo FROM " + ground.getFullname()+ ";");

			}else {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHead " +
			LocalDate.parse(fixture.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")).
			format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toUpperCase()+" - " 
						+ fixture.getTime()+" IST " + ";");	
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tBottomInfo FROM " +
						ground.getFullname()+ ";");

			}
		}
		  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 240.0;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		  print_writer.
		  println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
		  print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
		  TimeUnit.SECONDS.sleep(1);
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
		  print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
		  print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		
	}
	private void populateFF_EXCEL(String whatToProcess, String ValueToProcess, PrintWriter printWriter, List<Player> allPlayer) throws InterruptedException {
        Map<String, Object> rowData = KabaddiFunctions.ReadExcel(KabaddiUtil.FF_EXCEL).get(ValueToProcess);

        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHeader " + 
    	        (rowData.get("HEADER") != null ? rowData.get("HEADER").toString().trim() : "") + ";");
        
        if ((rowData.get("IS NAME?") != null ? rowData.get("IS NAME?").toString().trim() : "").toString().equalsIgnoreCase("Y")) {
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHead " +  (rowData.get("SUB HEADER") != null ? rowData.get("SUB HEADER").toString().trim() : "") + ";");
        } else {
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHead  ;");
        }
        
		int cols = rowData.get("COLS") != null ? (rowData.get("COLS") instanceof Number ? ((Number) rowData.get("COLS")).intValue() : Integer.parseInt(rowData.get("COLS").toString().trim())) : 0;
        int rows = rowData.get("ROWS") != null ? (rowData.get("ROWS") instanceof Number ? ((Number) rowData.get("ROWS")).intValue() : Integer.parseInt(rowData.get("ROWS").toString().trim())) : 0;
        
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectRows " + Math.abs(cols) + " ;");
        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectColumm " + (rows-1) + " ;");

        // Header of table
        for (int j = 1; j <= cols; j++) {
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatHead0" + j + " " + 
            		(rowData.get("H" + j) != null ? rowData.get("H" + j).toString().trim() : "") + ";");
        }

        // Body of table
        for (int i = 1; i <= rows; i++) {
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "A " + (rowData.get("R" + i + ".1") != null ? rowData.get("R" + i + ".1").toString().trim() : "") + ";");
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "B " + (rowData.get("R" + i + ".2") != null ? rowData.get("R" + i + ".2").toString().trim() : "") + ";");
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "C " + (rowData.get("R" + i + ".3") != null ? rowData.get("R" + i + ".3").toString().trim() : "") + ";");
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "D " + (rowData.get("R" + i + ".4") != null ? rowData.get("R" + i + ".4").toString().trim() : "") + ";");
            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tStatValue0" + i + "E " + (rowData.get("R" + i + ".5") != null ? rowData.get("R" + i + ".5").toString().trim() : "") + ";");

        }
        
	  printWriter.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 160.0;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
	  printWriter.
	  println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
	  printWriter.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
	  TimeUnit.SECONDS.sleep(1);
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
	  printWriter.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
	  printWriter.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");

}
	public ScoreBug populateScoreBug_Manual(boolean is_this_updating, ScoreBug scorebug, PrintWriter print_writer,
			Match match, String selectedbroadcaster) throws IOException {
			Map<String, Object> map = KabaddiFunctions.Read_Excel("C:\\Sports\\Kabaddi\\MANUAL_SCOREBUG.xlsx");
			if(is_this_updating == false) {
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tTopInfo " + map.get("HEADER") + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_HomeLogo " + logo_path + 
						map.get("HOME TEAM LOGO") + KabaddiUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lg_AwayLogo " + logo_path + 
						map.get("AWAY TEAM LOGO") + KabaddiUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeFirstName " +map.get("HOME TEAM NAME") + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayFirstName " +map.get("AWAY TEAM NAME")+ ";");
			}
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tVS_Score " +map.get("SCORE") + ";");

		
		return scorebug;
	}
	
	public ScoreBug populateTournamentLogo(boolean isThisUpdating, ScoreBug scorebug, PrintWriter printWriter,
	        Match match, String selectedBroadcaster) throws IOException {
	    if (match == null) {
	        System.out.println("ERROR: ScoreBug -> Match is null");
	        return scorebug;
	    }
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectLogo_Data 0;");
	   return scorebug;
	}
	public ScoreBug populateGoldenRaid(boolean isThisUpdating, ScoreBug scorebug, PrintWriter printWriter,
	        Match match, String selectedBroadcaster) throws IOException {
	    if (match == null) {
	        System.out.println("ERROR: ScoreBug -> Match is null");
	        return scorebug;
	    }
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectLogo_Data 3;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main$Select$Golden Raid$VS_Score$Time*FUNCTION*TIMER SET PAUSE INVOKE;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tGoldenTimer TIMER_OFFSET " + "30" + ";");
	   return scorebug;
	}
	
	public ScoreBug populateScoreLine(boolean isThisUpdating, ScoreBug scorebug, PrintWriter printWriter,
	        Match match, String selectedBroadcaster) throws IOException {
	    if (match == null) {
	        System.out.println("ERROR: ScoreBug -> Match is null");
	        return scorebug;
	    }

	    if (!isThisUpdating) {
	    	printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectLogo_Data 2;");
	    	printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader "+ match.getMatchIdent() + ";");
		    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + 
		            match.getHomeTeam().getTeamBadge() + KabaddiUtil.PNG_EXTENSION + ";");
		    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + 
		            match.getAwayTeam().getTeamBadge() + KabaddiUtil.PNG_EXTENSION + ";");
	    }

	    updateScore(printWriter, match);
	    
	    updateScorelineMatchClock(printWriter, match);

	    return scorebug;
	}
	
	public ScoreBug populateScoreBug(boolean isThisUpdating, ScoreBug scorebug, PrintWriter printWriter,
	        Match match, String selectedBroadcaster) throws IOException {
	    if (match == null) {
	        System.out.println("ERROR: ScoreBug -> Match is null");
	        return scorebug;
	    }

	    if (!isThisUpdating) {
	        updateStaticMatchInfo(printWriter, match);
	    }

	    updateScore(printWriter, match);
	    
	    updateMatchClock(printWriter, match);

	    updateRaiderInfo(printWriter, match);

	    updatePlayerStatus(printWriter, match);

	    return scorebug;
	}

	private void updateStaticMatchInfo(PrintWriter printWriter, Match match) {
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectLogo_Data 1;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getMatchIdent() + ";");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + 
	            match.getHomeTeam().getTeamBadge() + KabaddiUtil.PNG_EXTENSION + ";");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + 
	            match.getAwayTeam().getTeamBadge() + KabaddiUtil.PNG_EXTENSION + ";");

	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderClock 0;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderClock 0;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderName 0;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderName 0;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeRaiderName ;");
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayRaiderName ;");
	}

	private void updateMatchClock(PrintWriter printWriter, Match match) {
	    if (match.getClock() != null && match.getClock().getMatchHalves() != null) {
	        String matchHalf = match.getClock().getMatchHalves();
	        String subHeader;
	        int active_value = 1;
	        
	        switch (matchHalf) {
		        case "first":
	                subHeader = "1st HALF";
	                active_value = 1;
	                break;
	            case "second":
	                subHeader = "2nd HALF";
	                active_value = 1;
	                break;
	            case "extra1":
	                subHeader = "5-5 RAIDS";
	                active_value = 0;
	                break;
	            case "extra2":
	                subHeader = "GOLDEN RAID";
	                active_value = 0;
	                break;
	            case "half":
	                subHeader = "HALF TIME";
	                active_value = 1;
	                break;
	            case "full":
	                subHeader = "FULL TIME";
	                active_value = 1;
	                break;
	            default:
	                subHeader = "";
	                active_value = 0;
	                break;
	        }

	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main$Select$Data$Clock*CONTAINER SET ACTIVE " + active_value + ";");
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + subHeader + ";");
	    }
	}


	private void updateScore(PrintWriter printWriter, Match match) {
	    String score = match.getHomeTeamScore() + "-" + match.getAwayTeamScore();
	    if (match.getHomeTeamScore() == 0 && match.getAwayTeamScore() == 0) {
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vScoreVS 0 ;");
	    } else {
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vScoreVS 1 ;");
	    }
	    printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + score + ";");
	}

	private void updateRaiderInfo(PrintWriter printWriter, Match match) {
	    boolean isHomeRaiderIn = false;
	    boolean isAwayRaiderIn = false;

	    if (match.getApi_Match() != null) {
	        isHomeRaiderIn = updateRaiderName(printWriter, match, true);
	        isAwayRaiderIn = updateRaiderName(printWriter, match, false);
	    }

	    if (!isHomeRaiderIn) {
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderName 0;");
	    }

	    if (!isAwayRaiderIn) {
	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderName 0;");
	    }
	}

	private boolean updateRaiderName(PrintWriter printWriter, Match match, boolean isHomeTeam) {
	    List<PlayerStats> playerStatsList = isHomeTeam ? match.getApi_Match().getHomeTeamStats().getPlayerStats() 
	            : match.getApi_Match().getAwayTeamStats().getPlayerStats();
	    List<Player> squad = isHomeTeam ? match.getHomeSquad() : match.getAwaySquad();
	    String teamApiId = isHomeTeam ? match.getHomeTeam().getTeamApiId() : match.getAwayTeam().getTeamApiId();

	    for (PlayerStats plyr : playerStatsList) {
	        if (plyr.getPlayer_raiding_now() != null && plyr.getPlayer_raiding_now().equalsIgnoreCase("true")) {
	            if (Integer.parseInt(teamApiId) == Integer.valueOf(plyr.getPlayerId())) {
	                for (Player player : squad) {
	                    if (Integer.parseInt(player.getPlayerAPIId()) == Integer.valueOf(plyr.getPlayerId())) {
	                        String raiderName = player.getJersey_number() + " " + player.getTicker_name();
	                        if (isHomeTeam) {
	                            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderName 1;");
	                            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeRaiderName " + raiderName + ";");
	                        } else {
	                            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderName 1;");
	                            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayRaiderName " + raiderName + ";");
	                        }
	                        return true;
	                    }
	                }
	            }
	            break;
	        }
	    }
	    return false;
	}

	private void updatePlayerStatus(PrintWriter printWriter, Match match) {
	    if (match.getApi_Match() != null) {
	        for (int i = 1; i <= 7; i++) {
	            int homeStatus = i <= match.getApi_Match().getHomeTeamStats().getNo_of_players_on_court() ? 100 : 50;
	            int awayStatus = i <= match.getApi_Match().getAwayTeamStats().getNo_of_players_on_court() ? 100 : 50;
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePlayer" + i + " " + homeStatus + ";");
	            printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPlayer" + i + " " + awayStatus + ";");
	        }
	    }
	}

	private void updateScorelineMatchClock(PrintWriter printWriter, Match match) {
	    if (match.getClock() != null && match.getClock().getMatchHalves() != null) {
	        String matchHalf = match.getClock().getMatchHalves();
	        String subHeader;
	        
	        switch (matchHalf) {
	            case "first":
	                subHeader = "1st HALF";
	                break;
	            case "second":
	                subHeader = "2nd HALF";
	                break;
	            case "extra1":
	                subHeader = "5-5 RAIDS";
	                break;
	            case "extra2":
	                subHeader = "GOLDEN RAID";
	                break;
	            case "half":
	                subHeader = "HALF TIME";
	                break;
	            case "full":
	                subHeader = "FULL TIME";
	                break;
	            default:
	                subHeader = "";
	                break;
	        }

	        printWriter.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader03 " + subHeader + ";");
	    }
	}
	public void populateScoreLine(PrintWriter print_writer,String viz_sence_path,int side, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: ScoreBug -> Match is null");
		} else {
			
			Whichside = 3 - side;
			
			if(Whichside == 1) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side1*CONTAINER SET ACTIVE 1;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side2*CONTAINER SET ACTIVE 0;");
			}else if(Whichside == 2) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side1*CONTAINER SET ACTIVE 0;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side2*CONTAINER SET ACTIVE 1;");
			}
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side" + Whichside + 
					"$Score*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			
			if(match.getClock().getMatchHalves().equalsIgnoreCase("first") || match.getClock().getMatchHalves().equalsIgnoreCase("second")){
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side" + Whichside + 
						"$SubHead*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + match.getClock().getMatchHalves().toUpperCase() + " HALF" + ";");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("extra1a") || match.getClock().getMatchHalves().equalsIgnoreCase("extra1b")){
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side" + Whichside + 
						"$SubHead*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + "OVERTIME 1" + ";");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("extra2a") || match.getClock().getMatchHalves().equalsIgnoreCase("extra2b")){
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side" + Whichside + 
						"$SubHead*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + "OVERTIME 2" + ";");
			}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half") || match.getClock().getMatchHalves().equalsIgnoreCase("full")){
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side" + Whichside + 
						"$SubHead*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + match.getClock().getMatchHalves().toUpperCase() + " TIME" + ";");
			}else{
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$ScoreGfx$Side" + Whichside + "$SubHead*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + "" + ";");
			}
			
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$ScoreGfx$Side" + Whichside + "$Home$Logo*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$ScoreGfx$Side" + Whichside + "$Home$Shadow*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase()+ KabaddiUtil.PNG_EXTENSION +";");
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$ScoreGfx$Side" + Whichside + "$Away$Logo*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$ScoreGfx$Side" + Whichside + "$Away$Shadow*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			
			/*LAYER1*EVEREST*TREEVIEW*Main$All$MatchID$Side1$Away$Logo*TEXTURE1 SET TEXTURE_PATH C:/Images/PHL/Logos/DP.png;*/
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*ScoreIn SHOW 28.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*ScoreIn SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
		}
	}
	public void populateStartingLineUp(PrintWriter print_writer,String viz_sence_path,int team_id,int side, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: ScoreBug -> Match is null");
		} else {
			int row_id=0;
			Whichside = 3 - side;
			
			if(Whichside == 1) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side1*CONTAINER SET ACTIVE 1;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side2*CONTAINER SET ACTIVE 0;");
			}else if(Whichside == 2) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side1*CONTAINER SET ACTIVE 0;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side2*CONTAINER SET ACTIVE 1;");
			}
			
			if(team_id == match.getHomeTeamId()) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side" + Whichside + 
						"$Header*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + match.getHomeTeam().getTeamName1() + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$Teamlist$Side" + Whichside + "$Logos$LogoGrp$Logo*TEXTURE1 "
						+ "SET TEXTURE_PATH " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$Teamlist$Side" + Whichside + "$Logos$LogoGrp$Shadow*TEXTURE1 "
						+ "SET TEXTURE_PATH " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase()+ KabaddiUtil.PNG_EXTENSION +";");
				
				for(Player hs : match.getHomeSquad()) {
					row_id = row_id + 1;
					
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side" + Whichside + 
							"$Team$" + row_id + "$NumberGrp$Number*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + hs.getJersey_number() + ";");
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side" + Whichside + 
							"$Team$" + row_id + "$PlayerName*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + hs.getFull_name() + ";");
					
					
				}
				
			}else if(team_id == match.getAwayTeamId()) {
				row_id =0; 
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side" + Whichside + 
						"$Header*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + match.getAwayTeam().getTeamName1() + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$Teamlist$Side" + Whichside + "$Logo*TEXTURE1 "
						+ "SET TEXTURE_PATH " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$Teamlist$Side" + Whichside + "$Shadow*TEXTURE1 "
						+ "SET TEXTURE_PATH " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase()+ KabaddiUtil.PNG_EXTENSION +";");
				
				for(Player as : match.getAwaySquad()) {
					row_id = row_id + 1;
					
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side" + Whichside + 
							"$Team$" + row_id + "$NumberGrp$Number*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + as.getJersey_number() + ";");
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teamlist$Side" + Whichside + 
							"$Team$" + row_id + "$PlayerName*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + as.getFull_name() + ";");
					
					
				}
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*TeamListIn SHOW 28.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*TeamListIn SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
		}
	}
	public void populateTeamsLogos(PrintWriter print_writer,String viz_sence_path,int side,List<Team> teams, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: ScoreBug -> Match is null");
		} else {
			
			Whichside = 3 - side;
			
			if(Whichside == 1) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teams$Side1*CONTAINER SET ACTIVE 1;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teams$Side2*CONTAINER SET ACTIVE 0;");
			}else if(Whichside == 2) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teams$Side1*CONTAINER SET ACTIVE 0;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$Teams$Side2*CONTAINER SET ACTIVE 1;");
			}
			
			for(int i=0;i<=teams.size()-1;i++) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$Teams$Side" + Whichside + "$Logo" + (i+1) + "$LogoGrp$Logo*TEXTURE1 "
						+ "SET TEXTURE_PATH " + logo_path + teams.get(i).getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$Teams$Side" + Whichside + "$Logo" + (i+1) + "$LogoGrp$Shadow*TEXTURE1 "
						+ "SET TEXTURE_PATH " + logo_path + teams.get(i).getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION +";");
			}
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*TeamsIn SHOW 28.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*TeamsIn SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");
			
		}
	}
}
