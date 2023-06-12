# Plugin de login via CAS

# Prérequis
- Avoir java 17
- Avoir maven
- Avoir NMS sur son dépôt local
- (Eventuellement avoir un serveur minecraft)

# Comment build
- lancer la tâche `package` de maven.
- target/caslogin*.jar -> le .jar a mettre dans le dossier plugins du serveur minecraft

# Comment installer NMS
- Télécharger [BuildTools](https://www.spigotmc.org/wiki/buildtools/)
- Exécuter BuildTools.jar **avec l'option --remapped**

# Comment utiliser
- Avoir un server paper 1.19.4
- Avoir le .jar
- Avoir un serveur web d'authentification configuré -> [https://github.com/Eirbware/caslogin-mc-auth](https://github.com/Eirbware/caslogin-mc-auth)
- 