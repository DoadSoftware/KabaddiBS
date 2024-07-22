package com.kabaddi.containers;

public class ScoreBug {
	
	private boolean scorebug_on_screen;
	public boolean game_in = false;
	private boolean value_is_swap;
	public boolean start_pause;
	private String scorebug_stat;
	private String last_scorebug_stat;
	
	public ScoreBug() {
		super();
		this.game_in = false;
		this.value_is_swap=false;
	}
	
	public boolean isScorebug_on_screen() {
		return scorebug_on_screen;
	}
	public void setScorebug_on_screen(boolean scorebug_on_screen) {
		this.scorebug_on_screen = scorebug_on_screen;
	}
	public String getScorebug_stat() {
		return scorebug_stat;
	}
	public void setScorebug_stat(String scorebug_stat) {
		this.scorebug_stat = scorebug_stat;
	}
	public String getLast_scorebug_stat() {
		return last_scorebug_stat;
	}
	public void setLast_scorebug_stat(String last_scorebug_stat) {
		this.last_scorebug_stat = last_scorebug_stat;
	}
	public boolean isStart_pause() {
		return start_pause;
	}
	public void setStart_pause(boolean start_pause) {
		this.start_pause = start_pause;
	}
	public boolean isGame_in() {
		return game_in;
	}
	public void setGame_in(boolean game_in) {
		this.game_in = game_in;
	}
	public boolean isValue_is_swap() {
		return value_is_swap;
	}
	public void setValue_is_swap(boolean value_is_swap) {
		this.value_is_swap = value_is_swap;
	}
}
