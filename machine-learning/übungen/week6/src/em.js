#!/usr/bin/env node

const readline = require('readline')

const readData = async () => {
    const rl = readline.createInterface({
        input: process.stdin,
    });

    const points = [];

    for await (const line of rl) {
        const m = line.split(/\s+/).map((n) => parseFloat(n))
        points.push(m)
    }

    return points
}


const genCov = () => {
    const a = Math.random()
    const b = Math.random()
    const c = Math.random()
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
    return [Math.random(), Math.random()]
}

const p = (mu, sigma, x) => {
    const n = x.length
    const inv = [sigma[3],-sigma[1],-sigma[2],sigma[0]]
    const det = sigma[0]*sigma[3] - sigma[1] * sigma[2]
    const diff = [x[0]-mu[0], x[1]-mu[1]]

    const diff_inv_div = diff[0]*(inv[0]*diff[0] + inv[1]*diff[1]) +
                diff[1]*(inv[2]*diff[0] + inv[3]*diff[1])

    return 1 / Math.sqrt(Math.pow(Math.PI*2, n) * det) * Math.exp(-0.5 * diff_inv_div)
}

const expectationMaximisation = (K, points) => {
    const minX = Math.min(...points.map(p => p[0]))
    const maxX = Math.max(...points.map(p => p[0]))
    const minY = Math.min(...points.map(p => p[1]))
    const maxY = Math.max(...points.map(p => p[1]))
    const xScale = (maxX - minX);
    const yScale = (maxY - minY);

    const normPoints = points.map((p) => {
        return [
            (p[0] - minX) / (maxX - minX),
            (p[1] - minY) / (maxY - minY),
        ]
    })


    const pivots = Array(3).fill(true).map((_,i,a) => {
        return {
            phi: 1 / Math.pow(2,i+1) + 1/(Math.pow(2,a.length)*a.length),
            mu: genMean(),
            sigma: genCov(),
            weights: points.map((_,__,a) => null)
        }
    })

    let count = 50

    while(count--) {
        for(const piv of pivots) {
            piv.weights = normPoints.map((x) => p(piv.mu, piv.sigma, x))
        }


        for(const piv of pivots) {
            const weightSum = piv.weights.reduce((acc, w) => acc + w, 0)
            piv.phi = weightSum / piv.weights.length
            piv.mu = [
                piv.weights.reduce((acc, w, i) => acc + w * normPoints[i][0], 0) / weightSum,
                piv.weights.reduce((acc, w, i) => acc + w * normPoints[i][1], 0) / weightSum,
            ]
            piv.sigma = piv.weights.reduce(([a,b,c,d], w, i) => {
                const [x,y] = normPoints[i]
                const [mx,my] = piv.mu
                const [dX, dY] = [x-mx, y-my]

                return [
                    a + w * (dX*dX) / weightSum,
                    b + w * (dX*dY) / weightSum,
                    c + w * (dY*dX) / weightSum,
                    d + w * (dY*dY) / weightSum,
                ]
            }, [0,0,0,0])
        }
    }

    return pivots
}


readData().then((points) => {
    let bestResult = null

    const pivots = expectationMaximisation(3, points)

    const minX = Math.min(...points.map(p => p[0]))
    const maxX = Math.max(...points.map(p => p[0]))
    const minY = Math.min(...points.map(p => p[1]))
    const maxY = Math.max(...points.map(p => p[1]))
    const xScale = (maxX - minX);
    const yScale = (maxY - minY);

    for(pv of pivots) {
        console.log(minX + xScale * pv.mu[0], minY + yScale * pv.mu[1])
    }
})
