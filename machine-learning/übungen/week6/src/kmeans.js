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

const kMeans = (K, points) => {
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

    let centroids = Array(3).fill(true).map(() => {
        return {
            x: Math.random(),
            y: Math.random(),
            assignedPoints: [],
        }
    })

    let iterations = 0;

    do {
        var converge = true;
        iterations++;

        for(let c=0;c<centroids.length;c++) {
            centroids[c].assignedPoints.length = 0;
        }

        for(let p=0;p<normPoints.length;p++) {
            let [x,y] = normPoints[p]
            let assignedC = 0;

            for(let c=0;c<centroids.length;c++) {
                let oldDx = centroids[assignedC].x - x
                let oldDy = centroids[assignedC].y - y
                let newDx = centroids[c].x - x
                let newDy = centroids[c].y - y

                if(oldDx*oldDx + oldDy*oldDy > newDx*newDx+newDy*newDy) {
                    assignedC = c;
                }
            }

            centroids[assignedC].assignedPoints.push(p);
        }

        for(let c=0;c<centroids.length;c++) {
            let cx = 0;
            let cy = 0;
            const assignedPoints = centroids[c].assignedPoints

            for(let p=0;p<assignedPoints.length;p++) {
                const pnt = normPoints[assignedPoints[p]];
                const [x,y] = pnt
                cx += x
                cy += y
            }

            if(assignedPoints.length) {
                const newX = cx / assignedPoints.length;
                const newY = cy / assignedPoints.length;
                const deltaX = centroids[c].x - newX
                const deltaY = centroids[c].y - newY
                centroids[c].x = newX
                centroids[c].y = newY

                if(deltaX*deltaX + deltaY*deltaY > 0.5) {
                    converge = false
                }
            } else {
                centroids[c].x = 0
                centroids[c].y = 0
            }
        }
    } while(!converge)

    distortion = 0;

    for(let c=0;c<centroids.length;c++) {
        const assignedPoints = centroids[c].assignedPoints

        for(let p=0;p<assignedPoints.length;p++) {
            const [x,y] = normPoints[assignedPoints[p]]
            const dx = x - centroids[c].x
            const dy = y - centroids[c].y
            distortion += dx*dx + dy*dy
        }

        centroids[c].x = minX + xScale * centroids[c].x
        centroids[c].y = minY + yScale * centroids[c].y
    }

    return {iterations, centroids, distortion};
}

const ITERATIONS = 200;

readData().then((points) => {
    let bestResult = null

    for(let it=0;it<ITERATIONS;it++) {
        const result = kMeans(3, points)

        if(bestResult === null || bestResult.distortion > result.distortion) {
            bestResult = result;
        }

    }

    console.log("# Distortion", bestResult.distortion)
    console.log("# Centroids")

    for(let c=0;c<bestResult.centroids.length;c++) {
        console.log(bestResult.centroids[c].x, bestResult.centroids[c].y, '#', bestResult.centroids[c].assignedPoints.length);
    }

    for(let c=0;c<bestResult.centroids.length;c++) {
        console.log("")
        console.log("")
        for(let a=0;a<bestResult.centroids[c].assignedPoints.length;a++) {
            let [x,y] = points[bestResult.centroids[c].assignedPoints[a]];
            console.log(x,y)
        }
    }
})
