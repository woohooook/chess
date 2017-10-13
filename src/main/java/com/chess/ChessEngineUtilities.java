package com.chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ChessEngineUtilities {
	public static String getBastMove(String fen, Process p) throws IOException {
		OutputStream outputStream = p.getOutputStream();
		outputStream.write(fen.getBytes());
		outputStream.write("go depth 10\r\n".getBytes());
		outputStream.flush();
		// 取得命令结果的输出流
		InputStream fis = p.getInputStream();
		// 用一个读输出流类去读
		InputStreamReader isr = new InputStreamReader(fis);
		// 用缓冲器读行
		BufferedReader br = new BufferedReader(isr);
		String bestmove = null;
		while (true) {
			bestmove = br.readLine();
			// System.out.println(bestmove);
			if (bestmove.startsWith("bestmove"))
				return bestmove;
		}
	}
}
