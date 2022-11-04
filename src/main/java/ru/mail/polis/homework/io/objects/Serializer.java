package ru.mail.polis.homework.io.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Нужно реализовать методы этого класса и реализовать тестирование 4-ех (2-ух) способов записи.
 * Для тестирования надо создать список из 1000+ разных объектов (заполнять объекты можно рандомом,
 * с помощью класса Random).
 * Потом получившийся список записать в файл (необходимо увеличить размер списка, если запись происходит менее 5 секунд).
 * НЕ должно быть ссылок на одни и те же объекты
 *
 * Далее этот список надо прочитать из файла.
 *
 * Результатом теста должно быть следующее: размер файла, время записи и время чтения.
 * Время считать через System.currentTimeMillis().
 * В итоговом пулРеквесте должна быть информация об этих значениях для каждого теста. (всего 4 теста,
 * за каждый тест 1 балл)  и 3 балла за правильное объяснение результатов
 * Для тестов создайте класс в соответствующем пакете в папке тестов. Используйте другие тесты - как примеры.
 *
 * В конце теста по чтению данных, не забывайте удалять файлы
 */
public class Serializer {

    /**
     * 1 тугрик
     * Реализовать простую сериализацию, с помощью специального потока для сериализации объектов
     * @param animals Список животных для сериализации
     * @param fileName файл в который "пишем" животных
     */
    public void defaultSerialize(List<Animal> animals, String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fileNamePath))) {
            for (Animal animal : animals) {
                out.writeObject(animal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1 тугрик
     * Реализовать простую дисериализацию, с помощью специального потока для дисериализации объектов
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<Animal> defaultDeserialize(String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return null;
        }
        List<Animal> animals = new ArrayList<>();
        try (InputStream in = Files.newInputStream(fileNamePath);
             ObjectInputStream objectIn = new ObjectInputStream(in)) {
            while (in.available() > 0) {
                animals.add((Animal) objectIn.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return animals;
    }


    /**
     * 1 тугрик
     * Реализовать простую ручную сериализацию, с помощью специального потока для сериализации объектов и специальных методов
     * @param animals Список животных для сериализации
     * @param fileName файл в который "пишем" животных
     */
    public void serializeWithMethods(List<AnimalWithMethods> animals, String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fileNamePath))) {
            for (AnimalWithMethods animal : animals) {
                out.writeObject(animal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1 тугрик
     * Реализовать простую ручную дисериализацию, с помощью специального потока для дисериализации объектов
     * и специальных методов
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<AnimalWithMethods> deserializeWithMethods(String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return null;
        }
        List<AnimalWithMethods> animals = new ArrayList<>();
        try (InputStream in = Files.newInputStream(fileNamePath);
             ObjectInputStream objectIn = new ObjectInputStream(in)) {
            while (in.available() > 0) {
                animals.add((AnimalWithMethods) objectIn.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return animals;
    }

    /**
     * 1 тугрик
     * Реализовать простую ручную сериализацию, с помощью специального потока для сериализации объектов и интерфейса Externalizable
     * @param animals Список животных для сериализации
     * @param fileName файл в который "пишем" животных
     */
    public void serializeWithExternalizable(List<AnimalExternalizable> animals, String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return;
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fileNamePath))) {
            for (AnimalExternalizable animal : animals) {
                out.writeObject(animal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1 тугрик
     * Реализовать простую ручную дисериализацию, с помощью специального потока для дисериализации объектов
     * и интерфейса Externalizable
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<AnimalExternalizable> deserializeWithExternalizable(String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return null;
        }
        List<AnimalExternalizable> animals = new ArrayList<>();
        try (InputStream in = Files.newInputStream(fileNamePath);
             ObjectInputStream objectIn = new ObjectInputStream(in)) {
            while (in.available() > 0) {
                animals.add((AnimalExternalizable) objectIn.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return animals;
    }

    /**
     * 2 тугрика
     * Реализовать ручную сериализацию, с помощью высокоуровневых потоков. Сами ручками пишем поля,
     * без использования методов writeObject
     *
     * @param animals  Список животных для сериализации
     * @param fileName файл, в который "пишем" животных
     */
    public void customSerialize(List<Animal> animals, String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return;
        }
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(fileNamePath))) {
            for (Animal animal : animals) {
                out.writeUTF(animal.getAlias());
                out.writeInt(animal.getLegsCount());
                out.writeBoolean(animal.isPoisonous());
                out.writeBoolean(animal.isWild());
                Organization animalOrganization = animal.getOrganization();
                out.writeUTF(animalOrganization.getName());
                out.writeUTF(animalOrganization.getCountry());
                out.writeLong(animalOrganization.getLicenseNumber());
                out.writeUTF(animal.getGender().name());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2 тугрика
     * Реализовать ручную дисериализацию, с помощью высокоуровневых потоков. Сами ручками читаем поля,
     * без использования методов readObject
     *
     * @param fileName файл из которого "читаем" животных
     * @return список животных
     */
    public List<Animal> customDeserialize(String fileName) {
        Path fileNamePath = Paths.get(fileName);
        if (!Files.exists(fileNamePath)) {
            return null;
        }
        List<Animal> animals = new ArrayList<>();
        try (InputStream in = Files.newInputStream(fileNamePath);
             DataInputStream dataIn = new DataInputStream(in)) {
            while (in.available() > 0) {
                Animal animal = new Animal();
                animal.setAlias(dataIn.readUTF());
                animal.setLegsCount(dataIn.readInt());
                animal.setPoisonous(dataIn.readBoolean());
                animal.setWild(dataIn.readBoolean());
                Organization animalOrganization = new Organization();
                animalOrganization.setName(dataIn.readUTF());
                animalOrganization.setCountry(dataIn.readUTF());
                animalOrganization.setLicenseNumber(dataIn.readLong());
                animal.setOrganization(animalOrganization);
                animal.setGender(Gender.valueOf(dataIn.readUTF()));
                animals.add(animal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return animals;
    }
}
