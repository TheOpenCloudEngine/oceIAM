package org.opencloudengine.garuda.web.management;

import java.util.List;
import java.util.Map;

public interface ManagementRepository {
    String NAMESPACE = ManagementRepository.class.getName();

    int insert(Management management);

    Management selectById(Long id);

    Management selectByUserIdAndId(Long userId, Long id);

    List<Management> selectByUserId(Long userId);

    int updateById(Long id, String groupName, String description);

    int deleteById(Long id);
}
