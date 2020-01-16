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

const genMean = (minX, scaleX, minY, scaleY) => {
    return [minX + scaleX*Math.random(), minY + scaleY*Math.random()]
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
    const scaleX = (maxX - minX);
    const scaleY = (maxY - minY);

    const pivots = Array(3).fill(true).map((_,i,a) => {
        return {
            phi: 1 / Math.pow(2,i+1) + 1/(Math.pow(2,a.length)*a.length),
            mu: genMean(minX, scaleX, minY, scaleY),
            sigma: genCov(),
            weights: points.map((_,__,a) => null)
        }
    })

    let converged = true;

    do {

        for(const piv of pivots) {
            piv.weights = points.map((x) => piv.phi * p(piv.mu, piv.sigma, x) / pivots.reduce((ac, pv) => ac + pv.phi * p(pv.mu, pv.sigma, x), 0))
        }

        for(const piv of pivots) {
            const weightSum = piv.weights.reduce((acc, w) => acc + w, 0)
            const prevMu = piv.mu
            piv.phi = weightSum / piv.weights.length
            piv.mu = [
                piv.weights.reduce((acc, w, i) => acc + w * points[i][0], 0) / weightSum,
                piv.weights.reduce((acc, w, i) => acc + w * points[i][1], 0) / weightSum,
            ]
            piv.sigma = piv.weights.reduce(([a,b,c,d], w, i) => {
                const [x,y] = points[i]
                const [mx,my] = piv.mu
                const [dX, dY] = [x-mx, y-my]

                return [
                    a + w * (dX*dX) / weightSum,
                    b + w * (dX*dY) / weightSum,
                    c + w * (dY*dX) / weightSum,
                    d + w * (dY*dY) / weightSum,
                ]
            }, [0,0,0,0])

            converged = Math.abs(Math.abs(piv.mu[0]/prevMu[0] + piv.mu[1]/prevMu[1]) - 2) < 0.01
        }
    } while(!converged)

    return pivots
}


readData().then((points) => {
    let bestResult = null

    const pivots = expectationMaximisation(3, points)

    for(pv of pivots) {
        console.log("# phi=",pv.phi)
        console.log("# sigma=["+pv.sigma.join(',')+']')
        console.log(pv.mu[0], pv.mu[1])
        console.log("\n")
    }

    for(const pnt of points) {
        const ps = pivots.map(piv => piv.phi * p(piv.mu, piv.sigma, pnt));
        const totalPs = ps.reduce((a,x) => a+x, 0)
        console.log(pnt[0],pnt[1], ps.map(p => Math.round((p/totalPs)*1000) / 1000).join(' '))
    }
})
