#!/usr/bin/env node

const normal = (draws = 50) => {
    let result = 0;
    for(let x = 0;x<draws;x++) {
        result += Math.random()
    }

    return result / draws;
}

const cholesky2x2 = (matrix) => {
    return [
        Math.sqrt(matrix[0]),
        matrix[1] / Math.sqrt(matrix[0]),
        0,
        Math.sqrt(matrix[3] - Math.pow(matrix[2], 2) / matrix[0]),
    ]
}

const genCov = () => {
    const a = 25*Math.random()
    const b = 50*Math.random() - 25
    const c = 25*Math.random()
    const m = [a,b,
               b,c]

    return [
        a*a + b*b,
        a*b + b*c,
        b*a + c*b,
        b*b + c*c,
    ]
}

const genMean = () => {
    return [Math.random() * 2, Math.random() * 2]
}

const makeDistribution = (mean, cov) => {
    const cholesky = cholesky2x2(cov)


    return () => {
        const x = [
            normal(),
            normal(),
        ]

        return [
            mean[0] + cholesky[0] * x[0] + cholesky[1] * x[1],
            mean[1] + cholesky[2] * x[0] + cholesky[3] * x[1],
        ]
    }
}

const distributions = [
    makeDistribution(genMean(),genCov()),
    makeDistribution(genMean(),genCov()),
    makeDistribution(genMean(),genCov()),
]

const N = 100;

for(d of distributions) {
    for(let s=0;s<N;s++) {
        const p = d();
        console.log(p[0], p[1])
    }
}

