aws ecs update-service --cluster pocsoft --service sushi-api \
--task-definition sushi-api-task \
--desired-count 0 \
--profile folau \
--output text \
--no-cli-pager

read -t 1 -p "taking down api server."

aws rds stop-db-instance --profile folau \
--db-instance-identifier pocsoft-db \
--output text \
--no-cli-pager

read -t 1 -p "taking down postgres server."

