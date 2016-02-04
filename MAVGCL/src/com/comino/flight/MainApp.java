/*
 * Copyright (c) 2016 by E.Mansfeld
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comino.flight;

import java.io.IOException;
import java.util.Map;

import com.comino.flight.analysis.FlightAnalysisController;
import com.comino.flight.widgets.status.StatusWidget;
import com.comino.mav.control.IMAVController;
import com.comino.mav.control.impl.MAVSerialController;
import com.comino.mav.control.impl.MAVUdpController;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private IMAVController control = null;

	private Stage primaryStage;
	private BorderPane rootLayout;

	@FXML
	private MenuItem m_close;


	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("MAVGCL Analysis");

		String ipAddress = null;

		Map<String,String> args = getParameters().getNamed();

		if(args.size()> 0)
			ipAddress = args.get("udp");


		if(ipAddress==null)
			control = new MAVSerialController();
		else
			control = new MAVUdpController(ipAddress);

		if(!control.isConnected())
			control.connect();

		initRootLayout();
		showMAVGCLApplication();



	}

	@Override
	public void stop() throws Exception {
		control.close();
		super.stop();
		System.exit(0);
	}



	public static void main(String[] args) {
		launch(args);
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("analysis/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(getClass().getResource("analysis/application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.show();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void initialize() {

		m_close.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					stop();
				} catch (Exception e) {
					System.exit(-1);
				}

			}

		});

	}

	public void showMAVGCLApplication() {



		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("analysis/MAVGCL.fxml"));
			AnchorPane flightPane = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(flightPane);

			FlightAnalysisController fvController = loader.getController();
			fvController.start(this,control);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}