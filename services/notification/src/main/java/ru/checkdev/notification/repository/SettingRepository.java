/**
 *
 */
package ru.checkdev.notification.repository;

import org.springframework.data.repository.CrudRepository;
import ru.checkdev.notification.domain.http.Setting;

/**
 * @author olegbelov
 */
public interface SettingRepository extends CrudRepository<Setting, Integer> {
    Setting findByKey(Setting.Key key);
}
