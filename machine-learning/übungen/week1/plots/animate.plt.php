<?php ob_start(); ?>
set xrange [-0.1:1.1]
set yrange [-1.5:1.5]

<?php $skip = (int)($argv[1]?? 1); ?>
<?php $i = 0; ?>

<?php while($line = fgets(STDIN)): ?>
<?php if(strpos($line, '#') === 0) continue; ?>
<?php if($i++ % $skip !== 0) continue; ?>
<?php $cols = explode(' ', $line); ?>
<?php $it = array_shift($cols); ?>
<?php $err = array_shift($cols); ?>
set title "Iteration <?php echo $i; ?>"
polynomial(x) = <?php echo implode(' + ', array_map(function($k, $e) {
    return sprintf('%f* x**%d', $k, $e);
}, $cols, range(0, count($cols)))); ?>

plot "var/points.txt" using 1:2 title "data", \
    polynomial(x) lw 4 lt -1 title "$f(x)$", \
    sin(2 * pi * x) lw 2  title "$sin(2\\pi x)$"
<?php ob_flush(); ?>
<?php endwhile; ?>

set output
