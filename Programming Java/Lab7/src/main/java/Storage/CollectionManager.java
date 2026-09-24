package main.java.Storage;

import main.java.Model.Organization;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class CollectionManager {
    private final Collection<Organization> store =
            java.util.Collections.synchronizedCollection(new PriorityQueue<Organization>());
    private final ZonedDateTime initTime = ZonedDateTime.now();

    public CollectionManager() {
    }

    public CollectionManager(List<Organization> loaded) {
        synchronized (store) {
            store.addAll(loaded);
        }
    }

    public void replaceAll(List<Organization> fresh) {
        synchronized (store) {
            store.clear();
            store.addAll(fresh);
        }
    }

    public Organization getById(long id) {
        synchronized (store) {
            for (Organization o : store) if (o.getId() == id) return o;
            return null;
        }
    }

    public boolean removeById(long id) {
        synchronized (store) {
            return store.removeIf(o -> o.getId() == id);
        }
    }

    public List<Organization> getAsList() {
        synchronized (store) {
            return new ArrayList<>(store);
        }
    }

    public void add(Organization org) {
        synchronized (store) {
            store.add(org);
        }
    }

    public String show() {
        synchronized (store) {
            if (store.isEmpty()) return "Коллекция пуста.";
            return store.stream().map(Organization::toString).collect(Collectors.joining("\n"));
        }
    }

    public Organization removeHead() {
        synchronized (store) {
            if (store.isEmpty()) return null;
            Organization min = null;
            for (Organization o : store) if (min == null || o.compareTo(min) < 0) min = o;
            if (min != null) store.remove(min);
            return min;
        }
    }

    public boolean isMax(Organization c) {
        synchronized (store) {
            for (Organization o : store) if (c.compareTo(o) <= 0) return false;
            return true;
        }
    }

    public boolean isMin(Organization c) {
        synchronized (store) {
            for (Organization o : store) if (c.compareTo(o) >= 0) return false;
            return true;
        }
    }

    public boolean removeAnyByEmployeesCount(int n) {
        synchronized (store) {
            for (Organization o : store)
                if (o.getEmployeesCount() == n) {
                    store.remove(o);
                    return true;
                }
            return false;
        }
    }

    public String filterByAnnualTurnover(int t) {
        synchronized (store) {
            for (Organization o : store)
                if (o.getAnnualTurnover() != null && o.getAnnualTurnover() == t) return o.toString();
            return "Нет организаций с annualTurnover = " + t;
        }
    }

    public boolean removeFirst() {
        return removeHead() != null;
    }

    public int sizeInt() {
        synchronized (store) {
            return store.size();
        }
    }

    public String size() {
        return String.valueOf(sizeInt());
    }

    public String clear() {
        synchronized (store) {
            store.clear();
        }
        return "Коллекция очищена";
    }

    public boolean updateByIdBool(long id, Organization upd) {
        synchronized (store) {
            Organization rm = null;
            for (Organization o : store)
                if (o.getId() == id) {
                    rm = o;
                    break;
                }
            if (rm == null) return false;
            store.remove(rm);
            store.add(upd);
            return true;
        }
    }

    public String updateById(long id, Organization upd) {
        return updateByIdBool(id, upd) ? "true" : "false";
    }

    public String info() {
        synchronized (store) {
            java.time.format.DateTimeFormatter fmt =
                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

            return "Тип коллекции: PriorityQueue\n" +
                    "Дата инициализации: " + initTime.format(fmt) + "\n" +
                    "Количество элементов: " + store.size();
        }
    }
}
