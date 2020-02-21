package br.unb.cic.comnet.streaming.basin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import br.unb.cic.comnet.streaming.basin.configuration.BasinBeansConfiguration;
import br.unb.cic.comnet.streaming.basin.services.FFmpegService;
import br.unb.cic.comnet.streaming.basin.services.TranscodingDataUnit;
import br.unb.cic.comnet.streaming.basin.services.URLForTranscoding;

@SpringBootTest
@Import(BasinBeansConfiguration.class)
public class TestURLForTranscoding {
	
	@Autowired
	private FFmpegService ffmpegService;

	
	@Test
	public void testar() throws MalformedURLException, InterruptedException {
		String url = "https://video-weaver.sao01.hls.ttvnw.net/v1/playlist/CtAEhYlwoewCy407mA9X7fothcvBfxD9CEyh-xXSSrA79XlVVnX2ux8L_w8IZjDVU_cl5brnZNIXwSi-SBu9_VwflvbqWqNomvG9iJE_H5xkLiooNl0gWLW7aVsQVnqp3bia2JzNCQIK0FyThfrOdL1KJxZyLvfS5_M_7XRsED-EfBA810drEg1HEA0BW9I8302W49h0WZ7eaX2LIw12z39q0LyCZ3AN1n0fJ1kYxXx_JWlwSwawzbPMzM607TUAyKHKVBKSunVStfodaHpCeBBgIM6tNLy1Ec6sPuX3W5eUq4Nc5v_2tVeYIjE5R3TRsKAfQCo_KkB4IjIZQ2_QxTZ7HmQTBSyBmdUzYN79bMuqdSOAc1UoBc-4DDP4-aoxgPheM6Sw5oNE_Fi6bLK87BPoqagsm7u8fG5cMZz8Wusb_kNYlFeFt-uoCusksvY7kKx5X7Brpvlids_iu4B9RdLeTKluSKbKZssCH03-DBPA1H2drX1aH3uCuRUrrtlordQ4wRBF9FWbOr_XCKmiPt5FHQaLn0sczVsDxTM73rEg4u5a0zNgWAd9SFv2TmEhPQ_qLN6OxJSJ_rsUmv31IZ2a4TkMo6PDix1U0AjOGOt_QHvnPXtQv0hngEAG_ZlJu_T6ZxPtaUABPO59hPtf1a0vPGzznk3JW_aDGXZgguhGUZ2A-ao5fQZow7j_dsVx8xU7inPKvqnv35ye5VGKt7MCZ57DEs-BTrtE1W1krRxWZpTvoqQJGI7IBtxISX7dDW8kGYBZwSa1OSQQ3yXnrG8_hxIQdQhOYC3eQRDVXPMwpqA6NhoMWfp2hACofAuSngWl.m3u8";
		TranscodingDataUnit unit = new TranscodingDataUnit(new URL(url));
		
		
		TimerTask task = new TimerTask() {
			private int index = 0;			
			@Override
			public void run() {
				try {
					if (unit.updateLinks()) {
						/*
						System.out.println(
								"Last Modified: " + unit.getLastModified() + 
								" Num of Links: " + unit.getNumOfLinks() + 
								" Next to transcode: " + unit.getNextToTranscode().getUrl()
						);
						*/
						System.out.println("transcoding...");
						URLForTranscoding forTranscoding = unit.getNextToTranscode();
						if (forTranscoding != null) {
							System.out.println("  Now transcoding file " + forTranscoding.getUrl());
							ffmpegService.reduceQuality(forTranscoding.getUrl(), index++);
							System.out.println("finished!");							
						}
					}
				} catch (IOException e) {
					System.out.println("Error: " + e.getMessage());
				}

			}
		};
		
		Timer timer = new Timer("timer");
		long delay = 3000L;
		timer.scheduleAtFixedRate(task, delay, delay);
		
		Thread.sleep(15 * delay);
	}
	
	@Test
	public void testarReduzirQualidade() throws IOException, InterruptedException {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		URL url = new URL("https://video-edge-c2b6fc.sao01.abs.hls.ttvnw.net/v1/segment/CpUE6Lq3aT24bPq-9a5QitqrwhiT-6xbplh6x-r8DUNXhOOMN5nxuT9cBie3Z5UYkT5kGNHr80e_eKrN_M-O1uoSwoyhNVVsoTjqii5aJpgIJxZy05E5PE9JlzCndvXW5_WbaTuFVKdvSTKcZhL4OrZTt6oWB_wy2bcOl1-0E3ibM-0lp688P5OjY0NQMGgHcyILKJA8ZDIHRJEPTBXrxGOrExj4p5M5bW_dc5HwuwibOraIU0MUF5zejNpvtXrk4Q4cmuCJEWUZzS7kJsMAGC7tCyAE7NeKjMabWmxt8gkGnl4DAbm1USf5JIGng-T_95ghXe-jxNnUtem1nF_FDqICxE2Dj9RBTg2VVBCd2Sl8hZOryy4KvdEJk9We4ZlUTKnMO8mHnkcArwSOTVaFhsg0vFbFtP_tng3VgFpFNFECFIgeGkpCcSAUhVCJUTOVJ_yILm_2NNW2h7Yk8onL6RjK0REJ2Y7H4wIMokETRXj8gIYj9dgaK1QiFKrf8tyci8Ae8LyZ7jlC2vPWf8kmRZOP4DPVQ2iQ9YBAnFMmPPY1C0iaFMrI2lTlgwtZu6FZyAE7iIzR_lIau6lCFL7fayzwMHGF5uuMVS1fJRdW8bQa5VkJXvDh3LhCfwB1z8WTCtOls0J3JgC2MXaqHBcWgCt47X5zXMunlnUIHVRdY83Ze2n6uIkkwQwrkT9-gp9EwCgLUWGnu2YSEBMNF0j7RMXqLxp3HCtxjvYaDBCHJXrbpLP-u82w_g.ts");
		ffmpegService.reduceQuality(url, 24);
		Thread.sleep(60000);		
	}
}
