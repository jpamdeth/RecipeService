# Upgrade an existing database to MySQL 8.4

Use a logical backup and a new Compose project so the 8.0 volume remains available for rollback. Run these commands from `recipebook/` in Bash with the old Compose project still available. If you used a custom project name for MySQL 8.0, pass that name to the first three Compose commands.

## Back up MySQL 8.0

Stop the app before taking the backup so there are no writes between the backup and cutover.

```bash
backup_file="$HOME/recipebook-8.0-$(date +%Y%m%d-%H%M%S).sql"
docker compose stop app
docker compose exec -T mysql sh -c 'export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"; exec mysqldump --user=root --single-transaction --set-gtid-purged=OFF --no-tablespaces "$MYSQL_DATABASE"' > "$backup_file" && test -s "$backup_file"
```

Confirm the dump command succeeded and keep the backup in a private location. Check whether the old schema already has a Flyway history table:

```bash
docker compose exec -T mysql sh -c 'export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"; exec mysql --user=root --database="$MYSQL_DATABASE" --execute="SHOW TABLES"'
```

Once the backup is ready, stop the old stack and retain its volume.

```bash
docker compose down
```

## Restore a Flyway-managed database

If the backup includes `flyway_schema_history`, restore the complete dump before starting the app.

```bash
docker compose -p recipebook84 up -d --wait mysql
docker compose -p recipebook84 exec -T mysql sh -c 'export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"; exec mysql --user=root --database="$MYSQL_DATABASE"' < "$backup_file"
docker compose -p recipebook84 up -d --build --wait app
```

## Import a database created by the old schema.sql

Start the app once against the new volume so Flyway creates the target schema, then stop it before importing. Restore the old dump into the separate `recipe_legacy` staging schema. The import requires the target to contain only the original Flyway seed rows.

```bash
docker compose -p recipebook84 up -d --build --wait
docker compose -p recipebook84 stop app
docker compose -p recipebook84 exec -T mysql sh -c 'export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"; exec mysql --user=root --execute="CREATE DATABASE recipe_legacy"'
docker compose -p recipebook84 exec -T mysql sh -c 'export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"; exec mysql --user=root --database=recipe_legacy' < "$backup_file"
docker compose -p recipebook84 exec -T mysql sh -c 'export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"; exec mysql --user=root --database="$MYSQL_DATABASE"' < db/import-legacy.sql
docker compose -p recipebook84 up -d --wait app
```

The import replaces the target seed rows with the backed-up data in one transaction. Legacy pantry stock stored as NULL becomes zero. Duplicate ingredient links, missing required link fields, and broken foreign keys cause the transaction to roll back. Resolve any reported conflicts in the staging schema before retrying. The backup and original volume provide the rollback copy.

After either restore, check `/actuator/health`, compare recipe and ingredient counts to the source, and confirm a representative recipe's ingredients and quantities before accepting writes. Use `docker compose -p recipebook84` for subsequent runs against the new volume.
