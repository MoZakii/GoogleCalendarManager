package com.Management.TaskManagement.Services;

import com.Management.TaskManagement.Models.Task;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CalendarService {
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String CALENDAR_ID = "b35c962b4514710ae633a10b410af5495ab7e2a593780f39f75368266b73a601@group.calendar.google.com";

    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR_EVENTS);
    private static final String SERVICE_ACCOUNT_FILE_PATH = "D:\\TaskManagement\\TaskManagement\\src\\main\\resources\\credentials.json";

    private static Calendar calendar = null;
    private static GoogleCredentials getCredentials() throws IOException {

        // Load service account credentials from JSON file
        InputStream in;
        try
        {
            in = new FileInputStream(SERVICE_ACCOUNT_FILE_PATH);
        }
        catch (FileNotFoundException exception)
        {
            throw new FileNotFoundException(STR."Resource not found: \{SERVICE_ACCOUNT_FILE_PATH}");
        }

        return GoogleCredentials.fromStream(in).createScoped(SCOPES);
    }

    private static Calendar getCalendar() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void submitOneToCalendar(Task task) throws GeneralSecurityException, IOException {
        if (calendar == null)
        {
            calendar = getCalendar();
        }
        try
        {
            Event newEvent = makeEvent(task);
            calendar.events().insert(CALENDAR_ID, newEvent).execute();
        }catch (IOException exception)
        {
            throw new UnknownHostException(exception.getMessage());
        }
    }

    public static void submitManyToCalendar(List<Task> tasks) throws GeneralSecurityException, IOException {
        for (Task task : tasks)
        {
            submitOneToCalendar(task);
        }
    }

    private static Event makeEvent(Task task)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Event event = new Event();
        event.setSummary(task.getSummary());
        event.setStart(new EventDateTime().setDateTime(DateTime.parseRfc3339(task.getStartDate().format(formatter))));
        event.setEnd(new EventDateTime().setDateTime(DateTime.parseRfc3339(task.getEndDate().format(formatter))));
        return event;
    }
}
