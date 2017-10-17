package com.chess;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;

import com.chess.command.StopCommand;
import com.chess.utilities.ChessEngineUtilities;
import com.chess.utilities.RobotUtilities;

public class StartChessEngine implements Runnable {
	private Process p;
	public static long updateTime = System.currentTimeMillis();
	static {
		PropertyConfigurator.configure("log4j.properties");
	}

	public StartChessEngine(Process p) {
		this.p = p;
	}

	@Override
	public void run() {
		try {
			while (true) {
				// 处理准备开始，悔棋，求和。保证当前局面已到我方开始走棋,返回当前棋盘局面。
				String fen = RobotUtilities.prepareToPlay();
				// 通过fen获取最佳下法
				String bestmove = ChessEngineUtilities.getBastMove(fen, p);
				// 通过最佳下法操作鼠标点击
				RobotUtilities.playByBestmove(bestmove);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		// 使引擎在空闲状态
		final String cmd = "chessEngine\\XQSPIRIT.exe";
		Process p = Runtime.getRuntime().exec(cmd);
		final OutputStream outputStream = p.getOutputStream();
		outputStream.write("ucci\r\n".getBytes());
		outputStream.write("setoption Hash 520\r\n".getBytes());
		outputStream.flush();
		// 开始检测定时器，要注意根据限时设置时间，要不会影响引擎的棋力
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new StopCommand(p), 0, 2000, TimeUnit.MILLISECONDS);
		// 开始挂机线程
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new StartChessEngine(p));
	}
}
