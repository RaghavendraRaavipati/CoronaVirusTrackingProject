package com.raghu.coronavirustracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.raghu.coronavirustracker.modals.CoronaVirusDataEntity;

@Service
public class CoronaVirusTrackerService {

	private String Data_Url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

	private List<CoronaVirusDataEntity> allStats = new ArrayList<>();

	public List<CoronaVirusDataEntity> getAllStats() {
		return allStats;
	}
	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchData() throws ClientProtocolException, IOException {
		List<CoronaVirusDataEntity> newStats = new ArrayList<>();

		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpGet getRequest = new HttpGet(Data_Url);

		HttpResponse response = httpClient.execute(getRequest);

		HttpEntity httpEntity = response.getEntity();

		String apiOutput = EntityUtils.toString(httpEntity);

		StringReader csvBodyReader = new StringReader(apiOutput);

		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

		for (CSVRecord record : records) {

			CoronaVirusDataEntity coronaVirusDataEntity = new CoronaVirusDataEntity();

			coronaVirusDataEntity.setState(record.get("Province/State"));
			coronaVirusDataEntity.setCountry(record.get("Country/Region"));

			int latestCases = Integer.parseInt(record.get(record.size() - 1));
			int prevDayCases = Integer.parseInt(record.get(record.size() - 2));

			coronaVirusDataEntity.setLatestTotalCases(latestCases);
			coronaVirusDataEntity.setDiffFromPrevDay(latestCases - prevDayCases);

			newStats.add(coronaVirusDataEntity);
		}

		this.allStats = newStats;
	}

}


