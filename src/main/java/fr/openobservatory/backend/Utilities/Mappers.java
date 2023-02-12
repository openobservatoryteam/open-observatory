package fr.openobservatory.backend.Utilities;

import fr.openobservatory.backend.Implementations.Dtos.ObservationDto;
import fr.openobservatory.backend.Implementations.Dtos.UserDto;
import fr.openobservatory.backend.Implementations.Entities.Observation;
import fr.openobservatory.backend.Implementations.Entities.User;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class Mappers
{
    public static ObservationDto ObservationToDto(Observation observation)
    {
        //ToDo: Store the expiration delaly in a config file

        //add expiration delay to the date
        Timestamp ts = observation.getCreated_at();
        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        cal.add(Calendar.DAY_OF_WEEK, 30);
        ts = new Timestamp(cal.getTime().getTime());

        return new ObservationDto(observation.getId(),
                                observation.getDescription(),
                                observation.getLatitude(),
                                observation.getLongitude(),
                                observation.getOrientation(),
                                OffsetDateTime.ofInstant(ts.toInstant(), ZoneId.of("Europe/Paris")), //ToDo: Review
                                UserToDto(observation.getAuthor()));
    }

    public static UserDto UserToDto(User user)
    {
        return null;
    }
}
