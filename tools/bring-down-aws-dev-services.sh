aws ecs update-service --cluster pocsoft --service sushi-api \
--task-definition sushi-api-task \
--desired-count 0 \
--profile folauk110 \
--output text \
--no-cli-pager

read -t 1 -p "taking down api server."

aws rds stop-db-instance --profile folauk110 \
--db-instance-identifier sushi-api-prod \
--output text \
--no-cli-pager

read -t 1 -p "taking down postgres server."

