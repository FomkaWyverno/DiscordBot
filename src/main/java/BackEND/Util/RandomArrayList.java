package BackEND.Util;


import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RandomArrayList<E> implements Iterable<E>  { // Имплементируем интерфейс Итерабле для использование в for each.

    private static final Random r = new Random();


    private Class<E> type;
    private final Object[] objects;
    private final int[] place;
    private int size = 0;

    public RandomArrayList(int initialCapacity,Class<E> type) {
        place = new int[initialCapacity];
        objects = new Object[initialCapacity];
        this.type = type;
    }


    public boolean add(E e) {
        while (true) {
            int number = r.nextInt(place.length);
            if (isNotHave(number)) {
                place[size] = number;
                objects[number] = e;
                size++;

                break;
            }
        }
        return true;
    }

    public int size() {
        return objects.length;
    }

    public void addAllMessage(List<Message> list) throws NotTypeString {
        if (!(this.type.equals(String.class))){
            throw new NotTypeString("Type this generic do not STRING");
        }
        for (Message m: list) {
               this.add((E) m.getContentRaw());
       }
    }

    private E get(int i) {
        return (E) objects[i];
    }

    private boolean isNotHave(int number) {
        for (int i = 0; i < size; i++) {
            if(place[i]==number) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() { // Возращаем итератор
        return new Itr();
    }


    private class Itr implements Iterator<E> { // Шаблон для Итерации

        int cursor;

        int lastGet = -1;

        @Override
        public boolean hasNext() {
            return objects.length != cursor;
        }

        @Override
        public E next() {
            int i = cursor;
            if (i >= objects.length) {
                throw new NoSuchElementException();
            }
            Object[] o = objects;
            if (i >= o.length) {
                throw new ConcurrentModificationException();
            }
            cursor = i + 1;

            return (E) objects[lastGet = i];
        }
    }
}

