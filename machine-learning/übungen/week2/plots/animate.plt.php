<?php ob_start(); ?>
<?php $skip = (int)($argv[1]?? 1); ?>
<?php $i = 0; ?>

<?php while($line = fgets(STDIN)): ?>
<?php if(strpos($line, '#') === 0) continue; ?>
<?php if($i++ % $skip !== 0) continue; ?>
<?php $cols = array_map('floatval', explode(' ', $line)); ?>
<?php $it = array_shift($cols); ?>
<?php list($a,$b,$c) = $cols; ?>

set title "Iteration <?php echo $i; ?>"

f(x) = (<?php echo $a ?> + <?php echo $b ?>*x) * (-1/<?php echo $c ?>)

plot 'var/data.txt' using 1:($3==0 ? $2 : (1/0)) title "" ps 2 pt 1, \
     'var/data.txt' using 1:($3==1 ? $2 : (1/0)) title "" ps 2 pt 2, \
     f(x) with lines linestyle 5 title "$z(x) = 0$"

pause 0.1
<?php ob_flush(); ?>
<?php endwhile; ?>

set output
