# Plugin de login via CAS

# Prérequis
- Avoir java 17
- Avoir maven
- Un serveur [caslogin-auth](https://github.com/Eirbware/caslogin-mc-auth)
- (Eventuellement avoir un serveur minecraft)

# Structure
- /velocity
  - Le plugin qui tourne sur le proxy. Il s'occupe d'authentifier les utilisateurs afin de les transférer sur les serveurs avec l'identité authentifiée du CAS
- /compatfix
  - Le plugin qui tourne sur **les** serveurs qui sont censés récupérer l'utilisateur authentifié par le CAS. Ce plugin est nécessaire pour que le mode spectateur fonctionne correctement. (Il peut y avoir d'autres disfonctionnements liés à l'authentification, donc ce plugin est **impératif**). Ce plugin nécessite [Paper](https://papermc.io/software/paper)

# Build
- ./gradlew build
- velocity/build/libs/velocity*.jar pour le plugin velocity
- compatfix/build/libs/compatfix*.jar pour le plugin compatfix

# Config
- config.yml
  - auth_server
    - L'adresse du serveur [caslogin-auth](https://github.com/Eirbware/caslogin-mc-auth)
  - api_key
    - la clé API du serveur [caslogin-auth](https://github.com/Eirbware/caslogin-mc-auth)
  - entrypoint_server
    - Le serveur où les joueurs non authentifiés seront dirigés.
  - logged_entrypoint_server
    - Le serveur où les joueurs authentifiés seront dirigés après authentification.
- lang.yml
  - Les messages envoyés au joueur.

# Commandes
- /cas login
  - Permet de se connecter
  - Accessible uniquement sur le serveur `entrypoint_server`
- /cas logout
  - Permet de se déconnecter
  - Accessible uniquement sur les serveurs autres que `entrypoint_server`
- /cas config reload
  - Recharge la configuration.