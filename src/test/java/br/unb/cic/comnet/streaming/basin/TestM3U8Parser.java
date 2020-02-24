package br.unb.cic.comnet.streaming.basin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;

import br.unb.cic.comnet.streaming.basin.flexM3u8IO.FlexiblePlayListReader;
import br.unb.cic.comnet.streaming.basin.flexM3u8IO.FlexiblePlayListWriter;
import br.unb.cic.comnet.streaming.basin.flexM3u8IO.PlayList;

public class TestM3U8Parser {
	@Test
	public void testarReaderLocal() throws MalformedURLException, IOException {
		PlayList playList = new FlexiblePlayListReader().read(new URL("file:///C:/temp/Twitch-3mu8-file.txt").openStream());
		playList.getExtraInfo();
	}
	
	@Test
	public void testarReaderRemoto() throws MalformedURLException, IOException {
		PlayList playList = new FlexiblePlayListReader().read(new URL("https://video-weaver.sao01.hls.ttvnw.net/v1/playlist/Cr0EH_M45p82FN0lf2CsSIipRbgbV92km3fYiRrP1gEYelCWaF2340ScW94oythdTYngyNzsRPvC6O9fByToUt3tKRzRAXgP0IEnlq9cp9FTVbrZbnm2loBsyGWEhfFuXxSC_rFeYGXk1WZYZbAhz_pd0_jjsz0OECMQy7RPFBueB5jPDdmDksUnV53fTB_tGYnwHrl_3wX-beOsl9P3jxUdWBPWlgn57sq4K6JN0jON0L_uwztBijg8OzgjUP83mw4_eSW7NLQ10cK0wa3Yg-GhsRiJL77H7RrjpjzAwbD6hQ5v1du_9-KZp1b6os4QPMrvdozZsC9mpA7OMeJmF4rXtpS7RoS9JqqG86aWiHtJjOlgrbCYXfpMiAdBUYSqsdts3eT8_VPqBTiIBrjwZLOVojlINo7uJQ61jrA1wOgFU38Uf7Z6Io9jY5LaMWqJY05Qe9qLIf0BNqAzONtLg719wuCYm1pjGoEXIk5Vzju5IqZgT2-78d0kUxrdWQlJhicJfHpCEKFb8eagZESRQrz53RFaUxjXASURc1Pep1YlmwLQJqM1aGdCZa2nPB2k3wCJmvdYcmqP6sjaXzqTOddBkTcnYJnEDIrWfduiCJFKje_zkoxS52pedqMVeAX6MzIhPcZhtYiYeMZQUjj2YukGHZ1fojJnLD-cGssMMLcWElcLtG0MosRDBUz6hu1FhAnxBpgxgrvlGw3i8mc8XnJ8vB8UfseutTJa_a2GP6pRIdVhtAmki7mdv1RG26LYEhAMnr6fxjpVYUSL9bOQ8yuRGgxCa77CQZuhqinQKNg.m3u8").openStream());
		PlayList newPlayList = playList.cloneWithoutSegments();
		new FlexiblePlayListWriter().write(newPlayList, new FileOutputStream(new File("c:\\temp\\novo.m3u8")));
		playList.getExtraInfo();
	}	
	
}
