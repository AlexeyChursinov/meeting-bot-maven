package ru.chursinov.meetingbot.repository;

import org.springframework.data.repository.CrudRepository;
import ru.chursinov.meetingbot.entity.UserProfileData;

public interface UsersProfileRepo extends CrudRepository<UserProfileData, String> {
    UserProfileData findByUseridAndDate(long userid, String date);
}
