package util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class LoadImageThreadPoolExecutor extends ThreadPoolExecutor {
	Stage stage;

	public LoadImageThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, Window owner) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		// TODO Auto-generated constructor stub
		stage = new Stage();
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setBackground(Background.EMPTY);
		// root.setStyle("-fx-background-color: transparent;");
		// root.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
		Scene scene = new Scene(root, 400, 400);
		scene.setFill(null);
		ProgressIndicator indicator = new ProgressIndicator();
		indicator.setProgress(-1);
		Label label = new Label("图片加载中...请稍候");
		label.setTextFill(Color.BLACK);
		root.getChildren().addAll(indicator, label);

		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.initOwner(owner);
		root.setDisable(true);
		Platform.runLater(new Runnable() {
			public void run() {
				stage.show();
			}
		});
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		// TODO Auto-generated method stub
		super.beforeExecute(t, r);

	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		// TODO Auto-generated method stub
		super.afterExecute(r, t);
		if (getActiveCount() <= 1) {
			Platform.runLater(new Runnable() {
				public void run() {
					stage.hide();
				}
			});

		}
	}

}
