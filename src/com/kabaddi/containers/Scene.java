package com.kabaddi.containers;

import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import com.kabaddi.util.KabaddiUtil;

public class Scene {
	
	private String scene_path;
	private String broadcaster;
	private String which_layer;
	
	public Scene() {
		super();
	}

	public Scene(String scene_path, String which_layer) {
		super();
		this.scene_path = scene_path;
		this.which_layer = which_layer;
	}
	
	public String getScene_path() {
		return scene_path;
	}

	public void setScene_path(String scene_path) {
		this.scene_path = scene_path;
	}
	
	public String getBroadcaster() {
		return broadcaster;
	}

	public void setBroadcaster(String broadcaster) {
		this.broadcaster = broadcaster;
	}

	public String getWhich_layer() {
		return which_layer;
	}

	public void setWhich_layer(String which_layer) {
		this.which_layer = which_layer;
	}

	public void scene_load(PrintWriter print_writer, String broadcaster) throws InterruptedException
	{
		switch (broadcaster.toUpperCase()) {
		case KabaddiUtil.KABADDI: case "KABADDI_GIPKL":case "KABADDI_GIPKL_AR":case "KABADDI_GIPKL_BS":
			switch(this.which_layer.toUpperCase()) {
			case KabaddiUtil.ONE:
				//System.out.println("Secne : " + this.scene_path);
				print_writer.println("LAYER1*EVEREST*SINGLE_SCENE LOAD " + this.scene_path + ";");
				
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In STOP;");
				print_writer.println("LAYER1*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			case KabaddiUtil.TWO:
				print_writer.println("LAYER2*EVEREST*SINGLE_SCENE LOAD " + this.scene_path + ";");
				
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In STOP;");
				print_writer.println("LAYER2*EVEREST*STAGE*DIRECTOR*In SHOW 0.0;");
				TimeUnit.MILLISECONDS.sleep(500);
				break;
			/*
			 * case KabaddiUtil.THREE:
			 * print_writer.println("LAYER3*EVEREST*SINGLE_SCENE LOAD " + this.scene_path +
			 * ";");
			 * 
			 * print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*In SHOW 88.00;");
			 * print_writer.println("LAYER3*EVEREST*STAGE*DIRECTOR*LOOP START;");
			 * TimeUnit.MILLISECONDS.sleep(500); break;
			 */
			}
			break;
		}
	}
}
