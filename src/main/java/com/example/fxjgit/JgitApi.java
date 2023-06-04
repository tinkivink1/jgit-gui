package com.example.fxjgit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TransportHttp;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class JgitApi {
    /**
     Метод initializeRepository() инициализирует новый Git-репозиторий по указанному пути.
     @param path путь к директории, где должен быть создан репозиторий
     @return объект Git, представляющий инициализированный репозиторий
     @throws IOException если происходит ошибка ввода-вывода при инициализации репозитория
     */
    public static Git initializeRepository(String path) {
        Git git = null;
        try {
            git = Git.init().setDirectory(new File(path)).call();
            System.out.println(git.toString());
        }
        catch (GitAPIException e){
            System.out.println(e.getStackTrace());
        }

        return git;
    }

    /**
     Метод openRepository() открывает существующий Git-репозиторий по указанному пути.
     @param path путь к директории, содержащей репозиторий
     @return объект Git, представляющий открытый репозиторий
     @throws IOException если происходит ошибка ввода-вывода при открытии репозитория
     */
    public static Git openRepository(String path){
        Git git = null;
        try {
            git = Git.open(new File(path));
            System.out.println(git.toString());
        }
        catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        return git;
    }

    /**

     Метод cloneRepository() клонирует удаленный Git-репозиторий по указанному URL в указанную локальную директорию.
     @param url URL удаленного репозитория, который требуется склонировать
     @param path путь к локальной директории, где должен быть создан клон репозитория
     @return объект Git, представляющий склонированный репозиторий
     @throws GitAPIException если происходит ошибка взаимодействия с Git API при клонировании репозитория
     */
    public static Git cloneRepository(String url, String path) {
        Git git = null;
        try {
            git = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(new File(path))
                    .call();
            System.out.println("Repository cloned successfully!");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return git;
    }

    /**
     Метод cloneRepository() клонирует удаленный Git-репозиторий по указанному URL в указанную локальную директорию,
     используя предоставленный провайдер учетных данных.
     @param credentialsProvider провайдер учетных данных для аутентификации при клонировании репозитория
     @param url URL удаленного репозитория, который требуется склонировать
     @param path путь к локальной директории, где должен быть создан клон репозитория
     @return объект Git, представляющий склонированный репозиторий
     @throws GitAPIException если происходит ошибка взаимодействия с Git API при клонировании репозитория
     */
    public static Git cloneRepository(CredentialsProvider credentialsProvider, String url, String path) {
        Git git = null;
        try {
            TransportConfigCallback transportConfigCallback = transport -> {
                if (transport instanceof TransportHttp) {
                    ((TransportHttp) transport).setCredentialsProvider(credentialsProvider);
                }
            };
            git = Git.cloneRepository()
                    .setURI(url)
                    .setTransportConfigCallback(transportConfigCallback)
                    .setDirectory(new File(path))
                    .call();
            System.out.println("Repository cloned successfully!");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return git;
    }

    /**
     Метод addFile() добавляет указанный файл или паттерн файлов в индекс Git-репозитория.
     @param git объект Git, представляющий репозиторий, в котором требуется добавить файлы
     @param filePattern путь к файлу или паттерн файлов, которые требуется добавить в индекс
     @throws GitAPIException если происходит ошибка взаимодействия с Git API при добавлении файлов в индекс
     */
    public static void addFile(Git git, String filePattern){
        AddCommand addCommand  = git.add();
        addCommand.addFilepattern(filePattern);
        try {
            addCommand.call();
        } catch (GitAPIException e) {
            System.out.println(e.getStackTrace());
        }

    }

    /**
     Метод checkStatus() проверяет статус файлов в рабочей директории Git-репозитория.
     @param git объект Git, представляющий репозиторий, для которого требуется проверить статус файлов
     @return объект Status, содержащий информацию о статусе файлов в рабочей директории
     @throws GitAPIException если происходит ошибка взаимодействия с Git API при проверке статуса файлов
     */
    public static Status checkStatus(Git git) {
        StatusCommand statusCommand = git.status();
        Status status = null;
        try {
            status = statusCommand.call();
        } catch (GitAPIException e) {
            System.out.println(e.getStackTrace());
        }

        return status;
    }

    /**

     Метод getAffectedFiles() возвращает список файлов, затронутых указанным коммитом в заданном репозитории.
     @param repository объект Repository, представляющий репозиторий, в котором требуется найти затронутые файлы
     @param commitId идентификатор коммита, для которого требуется найти затронутые файлы
     @return список строк, содержащий пути к файлам, затронутым коммитом
     @throws IOException если происходит ошибка ввода-вывода при получении информации о файлах
     */
    public static List<String> getAffectedFiles(Repository repository, ObjectId commitId) throws IOException {
        List<String> affectedFiles = new ArrayList<>();

        try (RevWalk revWalk = new RevWalk(repository)) {
            ObjectId commitObjectId = commitId;
            RevCommit commit = revWalk.parseCommit(commitObjectId);

            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    affectedFiles.add(treeWalk.getPathString());
                }
            }
        }
        return affectedFiles;
    }

    /**
     Метод getCurrentDiffs() возвращает список различий (diff) для указанного файла между рабочей директорией
     и индексом Git-репозитория.
     @param git объект Git, представляющий репозиторий, для которого требуется получить различия
     @param filename имя файла, для которого требуется получить различия
     @return список строк, содержащий различия для указанного файла
     @throws IOException если происходит ошибка ввода-вывода при получении различий
     */
    public static List<String> getCurrentDiffs(Git git, String filename) throws IOException {
        Repository repository = git.getRepository();
        // Get the latest commit
        RevCommit commit = getLatestCommit(repository);

        // Get the file content from the latest commit
        String lastCommitContent = getFileContentFromCommit(repository, commit, filename);

        // Get the current file content
        Path currentPath = Paths.get(git.getRepository().getDirectory() + "\\..\\", filename);
        String currentContent = new String(Files.readAllBytes(currentPath));

        // Compare the contents and generate the diff
        List<String> diffList = generateDiff(lastCommitContent, currentContent);

        return diffList;
    }

    /**
     Метод getFileContentFromCommit() возвращает содержимое указанного файла из указанного коммита в заданном репозитории.
     @param repository объект Repository, представляющий репозиторий, в котором находится файл
     @param commit объект RevCommit, представляющий коммит, из которого требуется получить содержимое файла
     @param fileName имя файла, для которого требуется получить содержимое
     @return содержимое указанного файла из указанного коммита
     @throws IOException если происходит ошибка ввода-вывода при получении содержимого файла
     */
    public static String getFileContentFromCommit(Repository repository, RevCommit commit, String fileName) throws IOException {
        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(fileName));

            if (treeWalk.next()) {
                ObjectId objectId = treeWalk.getObjectId(0);
                try (org.eclipse.jgit.lib.ObjectReader reader = repository.newObjectReader()) {
                    return new String(reader.open(objectId).getBytes());
                }
            }
        }

        return "";
    }

    /**

     Метод getLatestCommit() возвращает объект RevCommit, представляющий самый последний коммит в заданном репозитории.
     @param repository объект Repository, представляющий репозиторий, для которого требуется получить последний коммит
     @return объект RevCommit, представляющий самый последний коммит в репозитории
     @throws IOException если происходит ошибка ввода-вывода при получении последнего коммита
     */
    public static RevCommit getLatestCommit(Repository repository) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve("HEAD"));
            return commit;
        }
    }

    /**

     Метод findCommitByMessage() находит коммит в репозитории по указанному сообщению коммита.
     @param git объект Git, представляющий репозиторий, в котором требуется найти коммит
     @param commitMessage сообщение коммита, по которому требуется найти соответствующий коммит
     @return объект RevCommit, представляющий найденный коммит
     @throws IOException если происходит ошибка ввода-вывода при поиске коммита
     */
    public static RevCommit findCommitByMessage(Git git, String commitMessage) throws IOException {
        Repository repository = git.getRepository();

        try (RevWalk revWalk = new RevWalk(repository)) {
            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit commit : commits) {
                if (commit.getShortMessage().equals(commitMessage)) {
                    return commit;
                }
            }
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**

     Метод generateDiff() генерирует различия (diff) между двумя строками с содержимым файлов.
     @param oldContent старое содержимое файла
     @param newContent новое содержимое файла
     @return список строк, содержащий различия между старым и новым содержимым файлов
     */
    public static List<String> generateDiff(String oldContent, String newContent) {
        // Implement your own diff generation logic here
        // This can be done using libraries like diffutils or by implementing your own diff algorithm
        // Here's a simple example using string comparison
        List<String> diffList = new ArrayList<>();

        String[] oldLines = oldContent.split("\\r?\\n");
        String[] newLines = newContent.split("\\r?\\n");

        for (int i = 0; i < oldLines.length; i++) {
            if (i >= newLines.length || !oldLines[i].equals(newLines[i])) {
                diffList.add("- \t" + (i + 1) + ": " + oldLines[i]);
                if (i < newLines.length) {
                    diffList.add("+ \t" + (i + 1) + ": " + newLines[i]);
                }
            } else {
                diffList.add("  \t" + (i + 1) + ": " + oldLines[i]);
            }
        }

        if (oldLines.length < newLines.length) {
            for (int i = oldLines.length; i < newLines.length; i++) {
                diffList.add("+ \t" + (i + 1) + ": " + newLines[i]);
            }
        }

        return diffList;
    }

    /**

     Метод commit() создает коммит с указанным сообщением в заданном Git-репозитории.
     @param git объект Git, представляющий репозиторий, в котором требуется создать коммит
     @param commitMessage сообщение коммита
     @throws GitAPIException если происходит ошибка взаимодействия с Git API при создании коммита
     */
    public static void commit(Git git, String commitMessage) throws GitAPIException {
        CommitCommand commitCommand = git.commit();
        commitCommand.setMessage(commitMessage);
        commitCommand.call();
    }
}