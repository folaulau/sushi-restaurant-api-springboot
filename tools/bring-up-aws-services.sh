aws rds start-db-instance \
--profile folau \
--db-instance-identifier pocsoft-db \
--output text \
--no-cli-pager

read -t 180 -p "postgres is starting up. waiting for 3 mins before starting api server."

aws ecs update-service --cluster pocsoft --service sushi-api \
--task-definition sushi-api-task \
--desired-count 1 \
--profile folau \
--output text \
--no-cli-pager

read -t 0 -p "api server is starting up"