package com.kabaddi.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import com.kabaddi.model.Match;
import com.kabaddi.model.Player;
import com.kabaddi.model.PlayerStats;
import com.kabaddi.model.Team;
import com.kabaddi.service.KabaddiService;
import com.kabaddi.util.KabaddiUtil;
import com.kabaddi.containers.Scene;
import com.kabaddi.containers.ScoreBug;

public class KABADDI extends Scene {

	public String session_selected_broadcaster = "KABADDI";

	public ScoreBug scorebug = new ScoreBug();
	public String which_graphics_onscreen = "";
	public boolean is_infobar = false,is_home_raider_in = false,is_away_raider_in = false;
	public int team_id=0;
	public long last_date = 0;
	public int Whichside = 2;
	public String logo_path = "C:/Images/UPKL/Logos/";
	
	public KABADDI() {
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
	
	public Object ProcessGraphicOption(String whatToProcess, Match match, Match swapMatch, KabaddiService kabaddiService,PrintWriter print_writer, List<Scene> scenes, String valueToProcess)
			throws InterruptedException, NumberFormatException, MalformedURLException, IOException, JAXBException {
		switch (whatToProcess.toUpperCase()) {
		//ScoreBug
		case "POPULATE-SCOREBUG": case "POPULATE-SCORELINE": case "POPULATE-TOURNAMENT_LOGO": case "POPULATE-GOLDEN_RAID":
			
		switch (whatToProcess.toUpperCase()) {
		case "POPULATE-SCOREBUG": case "POPULATE-SCORELINE": case "POPULATE-TOURNAMENT_LOGO": case "POPULATE-GOLDEN_RAID":
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
		}
			
		case "ANIMATE-IN-SCOREBUG": case "CLEAR-ALL": case "ANIMATE-OUT-SCOREBUG": case "RESET-ALL-ANIMATION":
		case "ANIMATE-IN-SCORELINE": case "ANIMATE-OUT": case "ANIMATE-IN-TOURNAMENT_LOGO": case "ANIMATE-IN-GOLDEN_RAID":
			
			switch (whatToProcess.toUpperCase()) {

			case "ANIMATE-IN-SCOREBUG":
				processAnimation(print_writer, "In", "START", session_selected_broadcaster,1);
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
			case "RESET-ALL-ANIMATION":
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tAwayRaiderClock TIMER_OFFSET 30;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tHomeRaiderClock TIMER_OFFSET 30;");
				break;
			}
			break;
		}
		return null;
	}

	public void processAnimation(PrintWriter print_writer, String animationName,String animationCommand, String which_broadcaster,int which_layer) throws IOException
	{
		switch(which_broadcaster.toUpperCase()) {
		case "KABADDI":
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
	
	/*public ScoreBug populateScoreBug(boolean is_this_updating, ScoreBug scorebug, PrintWriter print_writer,
			Match match, String selectedbroadcaster) throws IOException {
		if (match == null) {
			System.out.println("ERROR: ScoreBug -> Match is null");
		} else {
			
//			LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tagID TIMER_DURATION 30;
//			LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tagID TIMER_DIRECTION 0; // 0 for backwards and 1 for forward.
//			LAYER1*EVEREST*TREEVIEW*Main$Select$Data$Clock*FUNCTION*TIMER SET STOP INVOKE;
//			LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL_TIMER SET tagID TIMER_SHOW_MINUTES 0; // 0 for Hide and 1 for Show.
			
			if(is_this_updating == false) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vSelectLogo_Data 1 ;");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + " " + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader " + match.getMatchIdent() + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgHomeTeamLogo " + logo_path + 
						match.getHomeTeam().getTeamBadge() + KabaddiUtil.PNG_EXTENSION + ";");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET lgAwayTeamLogo " + logo_path + 
						match.getAwayTeam().getTeamBadge() + KabaddiUtil.PNG_EXTENSION + ";");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderClock 0;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderClock 0;");
				
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderName 0;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderName 0;");

				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeRaiderName ;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayRaiderName ;");
			}
			
			if(match.getClock() != null && match.getClock().getMatchHalves() != null) {
				
//				if(match.getClock().getMatchTimeStatus().equalsIgnoreCase(KabaddiUtil.PAUSE)) {
//					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$Select$Data$Clock*FUNCTION*TIMER SET PAUSE INVOKE;");
//				}else if(match.getClock().getMatchTimeStatus().equalsIgnoreCase(KabaddiUtil.START)) {
//					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$Select$Data$Clock*FUNCTION*TIMER SET CONTINUE INVOKE;");
//				}
				
				if(match.getClock().getMatchHalves().equalsIgnoreCase(KabaddiUtil.FIRST)) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + "1st HALF" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase(KabaddiUtil.SECOND)) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + "2nd HALF" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("extra1")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + "ET 1" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("extra2")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + "ET 2" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("half")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + "HALF TIME" + ";");
				}else if(match.getClock().getMatchHalves().equalsIgnoreCase("full")) {
					print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tSubHeader02 " + "FULL TIME" + ";");
				}
			}
			
			
			if(match.getHomeTeamScore() == 0 && match.getAwayTeamScore() == 0) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vScoreVS 0 ;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			}else {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vScoreVS 1 ;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tScore " + match.getHomeTeamScore() + "-" + match.getAwayTeamScore() + ";");
			}
			
			
			if(match.getApi_Match() != null) {
				for(PlayerStats plyr : match.getApi_Match().getHomeTeamStats().getPlayerStats()) {
					if(plyr.getPlayer_raiding_now() != null && plyr.getPlayer_raiding_now().equalsIgnoreCase("true")) {
					    if(Integer.parseInt(match.getHomeTeam().getTeamApiId()) == Integer.parseInt(match.getApi_Match().getHomeTeam().getTeamApiId())) {
					    	for(Player hs : match.getHomeSquad()) {
					    		if(Integer.parseInt(hs.getPlayerAPIId()) == Integer.valueOf(plyr.getPlayerId())) {
					    			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderName 1;");
									print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tHomeRaiderName " + hs.getJersey_number() + " " + hs.getTicker_name() + ";");
									
									is_home_raider_in = true;
					    		}
					    	}
					    }
						break;
					}else {
						is_home_raider_in = false;
					}
				}
				
				
				for(PlayerStats plyr : match.getApi_Match().getAwayTeamStats().getPlayerStats()) {
					if(plyr.getPlayer_raiding_now() != null && plyr.getPlayer_raiding_now().equalsIgnoreCase("true")) {
						if(Integer.parseInt(match.getAwayTeam().getTeamApiId()) == Integer.parseInt(match.getApi_Match().getAwayTeam().getTeamApiId())) {
					    	for(Player as : match.getAwaySquad()) {
					    		if(Integer.parseInt(as.getPlayerAPIId()) == Integer.valueOf(plyr.getPlayerId())) {
					    			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderName 1;");
									print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET tAwayRaiderName " + 
											as.getJersey_number() + " " + as.getTicker_name() + ";");
									is_away_raider_in = true;
					    		}
					    	}
					    }
						break;
					}else {
						is_away_raider_in = false;
					}
				}
			}
			
			if(match.getApi_Match() != null) {
				for(int i=1;i<=7;i++) {
					if(i <= match.getApi_Match().getHomeTeamStats().getNo_of_players_on_court()) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePlayer" + i + " 100;");
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomePlayer" + i + " 50;");
					}
					
					if(i <= match.getApi_Match().getAwayTeamStats().getNo_of_players_on_court()) {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPlayer" + i + " 100;");
					}else {
						print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayPlayer" + i + " 50;");
					}
				}
			}
			
			if(is_home_raider_in == false) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vHomeRaiderName 0;");
			}
			
			if(is_away_raider_in == false) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main*FUNCTION*TAG_CONTROL SET vAwayRaiderName 0;");
			}
		}
		
		return scorebug;
	}*/
	
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
	public void populateMatchId(PrintWriter print_writer,String viz_sence_path,int side, Match match, String selectedbroadcaster) throws IOException, InterruptedException {
		if (match == null) {
			System.out.println("ERROR: ScoreBug -> Match is null");
		} else {
			
			Whichside = 3 - side;
			
			if(Whichside == 1) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$MatchID$Side1*CONTAINER SET ACTIVE 1;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$MatchID$Side2*CONTAINER SET ACTIVE 0;");
			}else if(Whichside == 2) {
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$MatchID$Side1*CONTAINER SET ACTIVE 0;");
				print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$MatchID$Side2*CONTAINER SET ACTIVE 1;");
			}
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$BG_GRP$MatchID$Side" + Whichside + 
					"$SubHead*GEOMETRY*TEXT_UTF8 SET TEXT_UTF8 " + match.getMatchIdent().toUpperCase() + ";");
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$MatchID$Side" + Whichside + "$Home$Logo*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$MatchID$Side" + Whichside + "$Home$Shadow*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getHomeTeam().getTeamName4().toUpperCase()+ KabaddiUtil.PNG_EXTENSION +";");
			
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$MatchID$Side" + Whichside + "$Away$Logo*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			print_writer.println("LAYER1*EVEREST*TREEVIEW*Main$All$MatchID$Side" + Whichside + "$Away$Shadow*TEXTURE1 "
					+ "SET TEXTURE_PATH " + logo_path + match.getAwayTeam().getTeamName4().toUpperCase() + KabaddiUtil.PNG_EXTENSION + ";");
			
			/*LAYER1*EVEREST*TREEVIEW*Main$All$MatchID$Side1$Away$Logo*TEXTURE1 SET TEXTURE_PATH C:/Images/PHL/Logos/DP.png;*/
			
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW ON;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out STOP;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 88.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*MatchID_In SHOW 28.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT_PATH C:/Temp/Preview.png;");
			print_writer.println("LAYER1*EVEREST*GLOBAL SNAPSHOT 1920 1080;");
			TimeUnit.SECONDS.sleep(1);
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*Out SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*MatchID_In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
			print_writer.println("LAYER1*EVEREST*GLOBAL PREVIEW OFF;");	
			
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
