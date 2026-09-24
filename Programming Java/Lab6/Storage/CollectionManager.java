package Lab5.Storage;

import Lab5.Model.IdGenerator;
import Lab5.Model.Organization;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final PriorityQueue<Organization> organizationQueue;

    public CollectionManager() {
        this.organizationQueue = new PriorityQueue<>();
    }

    public CollectionManager(List<Organization> loadedOrganizations) {
        this.organizationQueue = new PriorityQueue<>(Comparator.naturalOrder());
        Set<Long> ids = new HashSet<>();
        long maxId = 0;
        for (Organization org : loadedOrganizations) {
            if (!ids.add(org.getId())) {
                throw new IllegalArgumentException("Дублирующийся ID организации: " + org.getId());
            }
            if (org.getId() > maxId) {
                maxId = org.getId();
            }
        }
        IdGenerator.updateCounter(maxId);
        organizationQueue.addAll(loadedOrganizations);
    }

    public Organization getById(long id) {
        for (Organization org : organizationQueue) {
            if (org.getId() == id) {
                return org;
            }
        }
        return null;
    }

    public boolean removeById(long id) {
        return organizationQueue.removeIf(org -> org.getId() == id);
    }

    public List<Organization> getAsList() {
        return new ArrayList<>(organizationQueue);
    }

    public String addIfMax(Organization org) {
        Organization max = organizationQueue.peek();
        if (max == null || org.compareTo(max) > 0) {
            organizationQueue.add(org);
            return "Организация добавлена, она больше всех.";
        } else {
            return "Организация не добавлена — не является максимальной.";
        }
    }

    public String add(Organization org) {
        organizationQueue.add(org);
        return "Организация добавлена: " + org.getName();
    }

    public String show() {
        if (organizationQueue.isEmpty()) {
            return "Коллекция пуста.";
        }
        return organizationQueue.stream().map(Organization::toString).collect(Collectors.joining());
    }

    public Organization removeHead() {
        return organizationQueue.poll(); // Возвращает первый элемент или null, если пусто
    }

    public String addIfMin(Organization org) {
        Organization min = organizationQueue.peek();
        if (min == null || org.compareTo(min) < 0) {
            organizationQueue.add(org);
            return "Организация добавлена, она меньше всех.";
        } else {
            return "Организация не добавлена — она не меньше минимального.";
        }
    }

    public String removeAnyByEmployeesCount(int employeesCount) {
        for (Organization org : organizationQueue) {
            if (org.getEmployeesCount() == employeesCount) {
                organizationQueue.remove(org);
                return "true";
            }
        }
        return "false";
    }

    public String filterByAnnualTurnover(int turnover) {
        for (Organization org : organizationQueue) {
            if (org.getAnnualTurnover() != null && org.getAnnualTurnover() == turnover) {
                return org.toString();
            }
        }
        return "Нет организаций с annualTurnover = " + turnover;
    }

    public String removeFirst() {
        if (!organizationQueue.isEmpty()) {
            organizationQueue.poll();
            return "true";
        }
        return "false";
    }

    public String size() {
        return organizationQueue.size() + "";
    }

    public String clear() {
        organizationQueue.clear();
        return "Коллекция очищена";
    }

    public String remove(Organization org) {
        organizationQueue.remove(org);
        return "Удалено";
    }

    public String updateById(long id, Organization updatedOrg) {
        for (Organization org : organizationQueue) {
            if (org.getId() == id) {
                organizationQueue.remove(org);
                organizationQueue.add(updatedOrg);
                return "true";
            }
        }
        return "false";
    }

    public String info() {
        return "Тип коллекции: PriorityQueue\n" +
                "Количество элементов: " + organizationQueue.size();
    }
}