mkdir -p $1
i=0
for a in 0 1 2
do
  for b in 0 1 2
  do
    p[$i]=$a.$b
    i=`expr $i + 1`
  done
done
for a in ${p[@]}
do
  c="${a//[^0]}"
  if [ ${#c} -lt 4 ]
  then
    aql -c "truncate test"
    sleep 2
    ./scripts/ces-cli.sh -i spec/optimistic/read/$1-spec.xml -o $1/$a -m 100000  -p $a
  fi
done

