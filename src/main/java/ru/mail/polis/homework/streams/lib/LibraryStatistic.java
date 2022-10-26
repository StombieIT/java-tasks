package ru.mail.polis.homework.streams.lib;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
/**
 * Класс для работы со статистикой по библиотеке.
 * Оценка 5-ть баллов
 */
public class LibraryStatistic {

    /**
     * Вернуть "специалистов" в литературном жанре с кол-вом прочитанных страниц.
     * Специалист жанра считается пользователь который прочел как минимум 5 книг в этом жанре,
     * при этом читал каждую из них не менее 14 дней.
     * @param library - данные библиотеки
     * @param genre - жанр
     * @return - map пользователь / кол-во прочитанных страниц
     */
    public Map<User, Integer> specialistInGenre(Library library, Genre genre) {
        return library.getArchive()
                .stream()
                .filter(archivedData -> {
                    LocalDateTime takePlus14Days = archivedData.getTake()
                            .toLocalDateTime()
                            .plusDays(14);
                    LocalDateTime returned = archivedData.getReturned().toLocalDateTime();
                    return returned.isEqual(takePlus14Days) ||
                            returned.isAfter(takePlus14Days) ||
                            archivedData.getBook().getGenre() == genre;
                })
                .collect(Collectors.groupingBy(ArchivedData::getUser))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() >= 5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .mapToInt(archivedData -> archivedData.getBook().getPage())
                                .sum()
                ));
    }

    /**
     * Вернуть любимый жанр пользователя. Тот что чаще всего встречается. Не учитывать тот что пользователь читает в данный момент.
     * Если есть несколько одинаковых по весам жанров - брать в расчет то, что пользователь читает в данный момент.
     * @param library - данные библиотеки
     * @param user - пользователь
     * @return - жанр
     */
    public Genre loveGenre(Library library, User user) {
        return library.getArchive()
                .stream()
                .filter(archivedData -> user.equals(archivedData.getUser()) &&
                        user.getBook().getGenre() != archivedData.getBook().getGenre())
                .collect(Collectors.groupingBy(
                        archivedData -> archivedData.getBook().getGenre(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max((entry1, entry2) -> (int) (entry1.getValue() - entry2.getValue()))
                .orElseThrow(NoSuchElementException::new)
                .getKey();
    }

    /**
     * Вернуть список пользователей которые больше половины книг держали на руках более 30-ти дней. Брать в расчет и книги которые сейчас
     * пользователи держат у себя (ArchivedData.returned == null)
     * @param library - данные библиотеки
     * @return - список ненадежных пользователей
     */
    public List<User> unreliableUsers(Library library) {
        return library.getArchive()
                .stream()
                .collect(Collectors.groupingBy(ArchivedData::getUser))
                .entrySet()
                .stream()
                .filter(entry -> {
                    Long booksCount = entry.getValue().stream().count();
                    Long expiredBooksCount = entry.getValue()
                            .stream()
                            .filter(archivedData -> {
                                LocalDateTime takePlus30Days = archivedData.getTake()
                                        .toLocalDateTime()
                                        .plusDays(30);
                                // TODO: null considered
                                LocalDateTime returned = archivedData.getReturned()
                                        .toLocalDateTime();
                                return returned.isAfter(takePlus30Days);
                            })
                            .count();
                    return expiredBooksCount > (booksCount >> 1);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Вернуть список книг у которых страниц равно или больше чем переданное значение
     * @param library - данные библиотеки
     * @param countPage - кол-во страниц
     * @return - список книг
     */
    public List<Book> booksWithMoreCountPages(Library library, int countPage) {
        return library.getBooks()
                .stream()
                .filter(book -> book.getPage() >= countPage)
                .collect(Collectors.toList());
    }

    /**
     * Вернуть самого популярного автора в каждом жанре. Если кол-во весов у авторов одинаково брать по алфавиту.
     * @param library - данные библиотеки
     * @return - map жанр / самый популярный автор
     */
    public Map<Genre, String> mostPopularAuthorInGenre(Library library) {
        return library.getArchive()
                .stream()
                .collect(Collectors.groupingBy(
                        archivedData -> archivedData.getBook().getGenre(),
                        Collectors.groupingBy(
                                archivedData -> archivedData.getBook().getAuthor(),
                                Collectors.counting())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .entrySet()
                                .stream()
                                .max((entry1, entry2) -> (int) (entry1.getValue() - entry2.getValue()))
                                .orElseThrow(NoSuchElementException::new)
                                .getKey()));
    }
}
