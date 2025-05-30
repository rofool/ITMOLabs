package Lab5.Storage;

import Lab5.Model.Organization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс для управления коллекцией организаций.
 * Использует PriorityQueue для хранения организаций с возможностью сортировки.
 */
public class CollectionManager {
    private final PriorityQueue<Organization> organizationQueue;

    /**
     * Конструктор по умолчанию для создания пустой коллекции.
     * Использует сортировку по умолчанию (по количеству сотрудников).
     */
    public CollectionManager() {
        this.organizationQueue = new PriorityQueue<>();
    }

    /**
     * Конструктор для создания менеджера коллекции с предзагруженными организациями.
     * При этом проверяется уникальность ID организаций —
     * при обнаружении дублирующегося ID выбрасывается IllegalArgumentException.
     *
     * @param loadedOrganizations Список организаций для инициализации коллекции.
     * @throws IllegalArgumentException если обнаружены дублирующиеся ID организаций.
     */

    public CollectionManager(List<Organization> loadedOrganizations) {
        this.organizationQueue = new PriorityQueue<>(Comparator.naturalOrder());

        Set<Long> ids = new HashSet<>();
        for (Organization org : loadedOrganizations) {
            if (!ids.add(org.getId())) {
                throw new IllegalArgumentException("Дублирующийся ID организации: " + org.getId());
            }
        }

        organizationQueue.addAll(loadedOrganizations);
    }

    /**
     * Получает организацию по её ID.
     *
     * @param id Идентификатор организации.
     * @return Организация с заданным ID, или null, если не найдена.
     */
    public Organization getById(long id) {
        for (Organization org : organizationQueue) {
            if (org.getId() == id) {
                return org;
            }
        }
        return null;
    }

    /**
     * Удаляет организацию по её ID.
     *
     * @param id Идентификатор организации.
     * @return true, если организация была удалена, иначе false.
     */
    public boolean removeById(long id) {
        return organizationQueue.removeIf(org -> org.getId() == id);
    }

    /**
     * Возвращает коллекцию организаций как список.
     *
     * @return Список организаций.
     */
    public List<Organization> getAsList() {
        return new ArrayList<>(organizationQueue);
    }

    /**
     * Добавляет организацию в коллекцию, если она больше всех в коллекции.
     *
     * @param org Организация для добавления.
     */
    public void addIfMax(Organization org) {
        Organization max = organizationQueue.peek();
        if (max == null || org.compareTo(max) > 0) {
            organizationQueue.add(org);
            System.out.println("Организация добавлена, она больше всех.");
        } else {
            System.out.println("Организация не добавлена — не является максимальной.");
        }
    }

    /**
     * Добавляет организацию в коллекцию.
     *
     * @param org Организация для добавления.
     */
    public void add(Organization org) {
        organizationQueue.add(org);
        System.out.println("Организация добавлена: " + org.getName());
    }

    /**
     * Выводит все организации в коллекции.
     */
    public void show() {
        if (organizationQueue.isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }

        organizationQueue.forEach(System.out::println);
    }

    /**
     * Удаляет и возвращает первый элемент коллекции.
     *
     * @return Первый элемент или null, если коллекция пуста.
     */
    public Organization removeHead() {
        return organizationQueue.poll(); // вернёт и удалит "первый" элемент или null, если пусто
    }

    /**
     * Добавляет организацию в коллекцию, если она меньше всех в коллекции.
     *
     * @param org Организация для добавления.
     */
    public void addIfMin(Organization org) {
        Organization min = organizationQueue.peek();
        if (min == null || org.compareTo(min) < 0) {
            organizationQueue.add(org);
            System.out.println("Организация добавлена, она меньше всех.");
        } else {
            System.out.println("Организация не добавлена — она не меньше минимального.");
        }
    }

    /**
     * Удаляет организацию с заданным количеством сотрудников.
     *
     * @param employeesCount Количество сотрудников.
     * @return true, если организация была удалена, иначе false.
     */
    public boolean removeAnyByEmployeesCount(int employeesCount) {
        for (Organization org : organizationQueue) {
            if (org.getEmployeesCount() == employeesCount) {
                organizationQueue.remove(org); // удалим один
                return true;
            }
        }
        return false;
    }

    /**
     * Фильтрует организации по заданному annualTurnover.
     *
     * @param turnover Значение оборота для фильтрации.
     */
    public void filterByAnnualTurnover(int turnover) {
        boolean found = false;
        for (Organization org : organizationQueue) {
            if (org.getAnnualTurnover() != null && org.getAnnualTurnover() == turnover) {
                System.out.println(org);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Нет организаций с annualTurnover = " + turnover);
        }
    }

    /**
     * Удаляет первый элемент коллекции.
     *
     * @return true, если элемент был удалён, иначе false.
     */
    public boolean removeFirst() {
        if (!organizationQueue.isEmpty()) {
            organizationQueue.poll();
            return true;
        }
        return false;
    }

    /**
     * Возвращает количество элементов в коллекции.
     *
     * @return Количество элементов в коллекции.
     */
    public int size() {
        return organizationQueue.size();
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        organizationQueue.clear();
    }

    /**
     * Удаляет организацию из коллекции.
     *
     * @param org Организация для удаления.
     * @return true, если организация была удалена, иначе false.
     */
    public boolean remove(Organization org) {
        return organizationQueue.remove(org);
    }

    /**
     * Обновляет организацию по её ID.
     *
     * @param id         Идентификатор организации.
     * @param updatedOrg Новая организация для обновления.
     * @return true, если организация была обновлена, иначе false.
     */
    public boolean updateById(long id, Organization updatedOrg) {
        for (Organization org : organizationQueue) {
            if (org.getId() == id) {
                organizationQueue.remove(org);
                organizationQueue.add(updatedOrg);
                return true;
            }
        }
        return false;
    }

    /**
     * Выводит информацию о коллекции (тип и размер).
     */
    public void info() {
        System.out.println("Тип коллекции: PriorityQueue");
        System.out.println("Количество элементов: " + organizationQueue.size());
    }
}
