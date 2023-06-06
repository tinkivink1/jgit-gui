package com.example.fxjgit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class JgitApi {

    private static String secret = "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";
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
                    ((TransportHttp) transport).setCredentialsProvider(new UsernamePasswordCredentialsProvider(secret, ""));
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
    public static List<String> status(Git git) throws GitAPIException {
        Status status = git.status().call();
        List<String> affectedFiles = new ArrayList<>();

        // Получение измененных файлов
        status.getModified().forEach(file -> affectedFiles.add(file));

        // Получение добавленных файлов
        status.getAdded().forEach(file -> affectedFiles.add(file));

        // Получение удаленных файлов
        status.getRemoved().forEach(file -> affectedFiles.add(file));

        // Получение измененных невследимых файлов
        status.getUntracked().forEach(file -> affectedFiles.add(file));

        return affectedFiles;
    }

    /**
     * Извлекает изменения из удаленного репозитория и обновляет локальный репозиторий.
     *
     * @param git             объект Git, представляющий локальный репозиторий
     * @param remoteRepoUrl   URL удаленного репозитория
     * @param username        имя пользователя для аутентификации (если требуется)
     * @param password        пароль пользователя для аутентификации (если требуется)
     * @throws GitAPIException если произошла ошибка при выполнении операции извлечения
     */
    public static void pull(Git git, String remoteRepoUrl, String username, String password) throws GitAPIException {
        PullCommand pullCommand = git.pull();

        // Установка URL удаленного репозитория
        pullCommand.setRemote(remoteRepoUrl);

        // Установка учетных данных (если требуется аутентификация)
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(secret, "");
        pullCommand.setCredentialsProvider(credentialsProvider);

        // Извлечение изменений из удаленного репозитория в локальный репозиторий
        pullCommand.call();
    }

    /**
     * Получает изменения из удаленного репозитория в локальный репозиторий без автоматического слияния.
     *
     * @param git             объект Git, представляющий локальный репозиторий
     * @param remoteRepoUrl   URL удаленного репозитория
     * @param username        имя пользователя для аутентификации (если требуется)
     * @param password        пароль пользователя для аутентификации (если требуется)
     * @throws GitAPIException если произошла ошибка при выполнении операции получения изменений
     */
    public static void fetch(Git git, String remoteRepoUrl, String username, String password) throws GitAPIException {
        FetchCommand fetchCommand = git.fetch();

        // Установка URL удаленного репозитория
        fetchCommand.setRemote(remoteRepoUrl);

        // Установка учетных данных (если требуется аутентификация)
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(secret, "");
        fetchCommand.setCredentialsProvider(credentialsProvider);

        // Получение изменений из удаленного репозитория в локальный репозиторий
        fetchCommand.call();
    }

    /**
     * Отправляет изменения из локального репозитория в удаленный репозиторий.
     *
     * @param git             объект Git, представляющий локальный репозиторий
     * @param remoteRepoUrl   URL удаленного репозитория
     * @param username        имя пользователя для аутентификации (если требуется)
     * @param password        пароль пользователя для аутентификации (если требуется)
     * @throws GitAPIException если произошла ошибка при выполнении операции отправки
     */
    public static void push(Git git, String remoteRepoUrl, String username, String password) throws GitAPIException {
        PushCommand pushCommand = git.push();

        // Установка URL удаленного репозитория
        pushCommand.setRemote(remoteRepoUrl);

        // Установка учетных данных (если требуется аутентификация)
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(secret, "");
        pushCommand.setCredentialsProvider(credentialsProvider);

        // Отправка изменений из локального репозитория в удаленный репозиторий
        pushCommand.call();

        System.out.println("push performed");
    }

    /**

     Метод getAffectedFiles() возвращает список файлов, затронутых указанным коммитом в заданном репозитории.
     @param repository объект Repository, представляющий репозиторий, в котором требуется найти затронутые файлы
     @param commitId идентификатор коммита, для которого требуется найти затронутые файлы
     @return список строк, содержащий пути к файлам, затронутым коммитом
     @throws IOException если происходит ошибка ввода-вывода при получении информации о файлах
     */
    public static List<String> getAffectedFiles(Repository repository, ObjectId commitId) throws IOException, GitAPIException {
        List<String> affectedFiles = new ArrayList<>();

        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(commitId);
            RevTree parentTree = commit.getParentCount() > 0 ? revWalk.parseCommit(commit.getParent(0).getId()).getTree() : null;
            RevTree commitTree = commit.getTree();

            try (Git git = new Git(repository)) {
                List<DiffEntry> diffs = git.diff()
                        .setOldTree(getCanonicalTreeParser(repository, parentTree))
                        .setNewTree(getCanonicalTreeParser(repository, commitTree))
                        .call();

                for (DiffEntry diff : diffs) {
                    String oldPath = diff.getOldPath();
                    String newPath = diff.getNewPath();

                    switch (diff.getChangeType()) {
                        case ADD:
                            affectedFiles.add(newPath);
                            break;
                        case MODIFY:
                            affectedFiles.add(newPath);
                            break;
                        case DELETE:
                            affectedFiles.add(oldPath);
                            break;
                        case RENAME:
                            affectedFiles.add(oldPath);
                            affectedFiles.add(newPath);
                            break;
                    }
                }
            }
        }

        return affectedFiles;
    }

    private static CanonicalTreeParser getCanonicalTreeParser(Repository repository, RevTree tree) throws IOException {
        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        try (ObjectReader reader = repository.newObjectReader()) {
            treeParser.reset(reader, tree.getId());
        }
        return treeParser;
    }

    /**
     Метод getCurrentDiffs() возвращает список различий (diff) для указанного файла между рабочей директорией
     и индексом Git-репозитория.
     @param git объект Git, представляющий репозиторий, для которого требуется получить различия
     @param filename имя файла, для которого требуется получить различия
     @return список строк, содержащий различия для указанного файла
     @throws IOException если происходит ошибка ввода-вывода при получении различий
     */
    public static List<String> getCurrentDiffs(Git git, String filename) throws IOException, InvalidFormatException {
        Repository repository = git.getRepository();
        // Get the latest commit
        RevCommit commit = getLatestCommit(repository);

        // Get the file content from the latest commit
        String lastCommitContent = getFileContentFromCommit(repository, commit, filename);

        // Get the current file content
        Path currentPath = Paths.get(git.getRepository().getDirectory() + "\\..\\", filename);
        List<String> filecontent = FileReader.read(new File(currentPath.toString()));
        StringBuilder result = new StringBuilder();

        for (String str : filecontent) {
            result.append(str);
            result.append('\n');
        }

        String currentContent =  result.toString();
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

    public static void createBranch(Git git, String branchName) {
        try {
            Repository repository = git.getRepository();

            // Проверяем, что указанная ветка не существует
            Ref existingBranch = repository.findRef(branchName);
            if (existingBranch != null) {
                System.out.println("Ветка с именем " + branchName + " уже существует.");
                return;
            }

            // Создаем новую ветку
            git.branchCreate()
                    .setName(branchName)
                    .call();

            System.out.println("Ветка " + branchName + " успешно создана.");
        } catch (GitAPIException | IOException e) {
            System.out.println("Ошибка при создании ветки: " + e.getMessage());
        }
    }

    public static void switchToBranch(Git git, String branchName) {
        try {
            Repository repository = git.getRepository();

            // Проверяем, существует ли указанная ветка
            Ref branchRef = repository.findRef(branchName);
            if (branchRef == null) {
                System.out.println("Ветка с именем " + branchName + " не существует.");
                return;
            }

            // Переключаемся на указанную ветку
            CheckoutCommand checkoutCommand = git.checkout();
            checkoutCommand.setName(branchName);
            checkoutCommand.call();

            System.out.println("Успешно переключено на ветку " + branchName);
        } catch (GitAPIException e) {
            System.out.println("Ошибка при переключении на ветку: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getBranchList(Git git) {
        List<String> branchList = new ArrayList<>();

        try {
            List<Ref> branches = git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call();

            for (Ref branch : branches) {
                String branchName = branch.getName();
                branchList.add(branchName);
            }
        } catch (Exception e) {
            System.out.println("Error getting branch list: " + e.getMessage());
        }

        return branchList;
    }

    public static void deleteBranch(Git git, String branchName) {
        try {
            git.branchDelete()
                    .setBranchNames(branchName)
                    .call();

            System.out.println("Branch '" + branchName + "' deleted successfully.");
        } catch (GitAPIException e) {
            System.out.println("Error deleting branch: " + e.getMessage());
        }
    }

    public static void renameBranch(Git git, String oldBranchName, String newBranchName) {
        try {
            git.branchRename()
                    .setOldName(oldBranchName)
                    .setNewName(newBranchName)
                    .call();

            System.out.println("Branch '" + oldBranchName + "' renamed to '" + newBranchName + "' successfully.");
        } catch (GitAPIException e) {
            System.out.println("Error renaming branch: " + e.getMessage());
        }
    }

    public static String getRemoteRepositoryUrl(Git git, String remoteName) {
        try {
            Repository repository = git.getRepository();
            List<RemoteConfig> remoteConfigs = RemoteConfig.getAllRemoteConfigs(repository.getConfig());

            for (RemoteConfig remoteConfig : remoteConfigs) {
                if (remoteConfig.getName().equals(remoteName)) {
                    URIish uri = remoteConfig.getURIs().get(0);
                    return uri.toString();
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void addRemote(Git git, String remoteName, String remoteUrl) {
        try {
            git.remoteAdd()
                    .setName(remoteName)
                    .setUri( new URIish(remoteUrl))
                    .call();

            System.out.println("Remote '" + remoteName + "' added successfully.");
        } catch (GitAPIException e) {
            System.out.println("Error adding remote: " + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void reset(Git git) {
        try {
            git.reset()
                    .setMode(ResetCommand.ResetType.SOFT)
                    .setRef("HEAD^")
                    .call();

            System.out.println("Last commit reset successfully (soft reset).");
        } catch (GitAPIException e) {
            System.out.println("Error resetting last commit: " + e.getMessage());
        }
    }
}