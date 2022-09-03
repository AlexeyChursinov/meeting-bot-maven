package ru.chursinov.meetingbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.chursinov.meetingbot.entity.UserProfileData;
import ru.chursinov.meetingbot.repository.UsersProfileRepo;

@Service
public class UsersProfileDataService {

    private UsersProfileRepo usersProfileRepo;

    @Autowired
    public UsersProfileDataService(UsersProfileRepo usersProfileRepo) {
        this.usersProfileRepo = usersProfileRepo;
    }

    public void saveUserProfileData(UserProfileData userProfileData) {
        usersProfileRepo.save(userProfileData);
    }

    public UserProfileData getUserAnswer(long id, String date) {
        return usersProfileRepo.findByUseridAndDate(id, date);
    }

}
