package com.company.coronavirustrackerapp.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.company.coronavirustrackerapp.models.LocationStats;

@Service
public class CoronavirusDataService {

	private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/refs/heads/master/archived_data/archived_time_series/time_series_19-covid-Confirmed_archived_0325.csv";
	List<LocationStats> allStats=new ArrayList<>();
	
	
	public List<LocationStats> getAllStats() {
		return allStats;
	}

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException
	{
		List<LocationStats> newStats=new ArrayList<>();
		HttpClient client=HttpClient.newHttpClient();
		HttpRequest request=HttpRequest.newBuilder()
						.uri(URI.create(VIRUS_DATA_URL))
						.build();
		
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		System.out.println(httpResponse.body());
		StringReader csvBodyReader =new StringReader(httpResponse.body());
		Iterable<CSVRecord> records=CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		try {
		for(CSVRecord record:records) {
			LocationStats locationStats=new LocationStats();
			locationStats.setState(record.get("Province/State"));
			locationStats.setCountry(record.get("Country/Region"));
			
			int latestCases = Integer.parseInt(record.get(record.size()-1));
			int prevDayCases = Integer.parseInt(record.get(record.size()-2));
			locationStats.setLatestTotalCases(latestCases);
			locationStats.setDiffFromPrevDate(latestCases-prevDayCases);
			System.out.println(locationStats);
			newStats.add(locationStats);
		}
		}catch (Exception e) {
			
		}
		this.allStats = newStats;
	}
}
