mkdir -p $1
aql -c "truncate test"
sleep 2
scripts/ces-cli.sh -i spec/optimistic/write/$1-spec.xml -o $1