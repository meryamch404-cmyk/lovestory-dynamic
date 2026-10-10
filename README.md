# Love Story — première version dynamique

Le projet conserve les pages et les images d'origine et ajoute un serveur Java Spring Boot et une base de données H2 persistante pour :
- enregistrer les inscriptions via `POST /api/register` ;
- enregistrer les commandes via `POST /api/orders` ;
- hacher les mots de passe avec BCrypt ;
- conserver les données dans `./data/lovestory`.

## Prérequis
- Java 17 ou plus récent
- Maven 3.9+ (ou Maven Wrapper si vous l'ajoutez)

## Lancer dans VS Code
1. Décompressez le projet.
2. Ouvrez le dossier `lovestory-dynamic` dans VS Code.
3. Vérifiez `java -version` et `mvn -version` dans le terminal.
4. Lancez `mvn spring-boot:run`.
5. Ouvrez `http://localhost:8080/` (page d'accueil), puis testez l'inscription et les commandes.

Ne double-cliquez pas directement sur les pages HTML : elles doivent être servies par Spring Boot pour que les appels `/api/...` fonctionnent.

## Limites à connaître avant une mise en ligne commerciale
- Cette version est un point de départ pédagogique. Elle ne contient pas encore de connexion utilisateur, d'espace admin, de paiement en ligne ni de gestion de stock.
- La base H2 est pratique pour tester localement. Pour un déploiement public, configurez une base PostgreSQL/MySQL hébergée et ses identifiants via variables d'environnement.
- Les prix et produits sont validés côté serveur pour éviter de faire confiance aux valeurs envoyées par le navigateur.
- Avant usage réel, ajouter protection anti-abus, validation renforcée, politique de confidentialité, sauvegardes et tests de sécurité.
