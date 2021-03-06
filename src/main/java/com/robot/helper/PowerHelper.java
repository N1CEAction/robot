package com.robot.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.robot.bean.PowerEntity;
import com.robot.bean.repository.PowerEntityRepository;
import com.robot.entity.Order.Power;
import com.robot.entity.User;

@Component
public class PowerHelper {
    @Autowired
    private PowerEntityRepository powerEntityRepository;

    public Power getPowerByUserId(String userId) {
        PowerEntity findByUserId = powerEntityRepository.findByUserId(userId);
        if (findByUserId == null)
            return Power.USER;
        return Power.getById(findByUserId.getPowerId());
    }

    public void save(Power power, List<User> atUsers) {
        List<String> masterIds = new ArrayList<>();
        atUsers.forEach(user -> {
            String dingtalkId = user.getDingtalkId();
            PowerEntity findByUserId = powerEntityRepository.findByUserId(dingtalkId);
            if (findByUserId != null) {
                if (findByUserId.getPowerId() > 0)
                    powerEntityRepository.delete(findByUserId.getId());
                else
                    masterIds.add(findByUserId.getUserId());
            }
        });

        powerEntityRepository.save(atUsers.stream().filter(user -> !masterIds.contains(user.getDingtalkId()))
                .map(user -> new PowerEntity(0, user.getDingtalkId(), power.getId())).collect(Collectors.toList()));
    }
}
